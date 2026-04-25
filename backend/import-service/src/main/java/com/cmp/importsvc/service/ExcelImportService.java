package com.cmp.importsvc.service;

import com.cmp.importsvc.dto.BulkUpsertItem;
import com.cmp.importsvc.dto.BulkUpsertRequest;
import com.cmp.importsvc.dto.BulkUpsertResponse;
import com.cmp.importsvc.dto.ImportSummary;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExcelImportService {

    private static final int NAME_COL = 0;
    private static final int DOB_COL = 1;
    private static final int NIC_COL = 2;

    private final CustomerBulkClient customerBulkClient;

    public ExcelImportService(CustomerBulkClient customerBulkClient) {
        this.customerBulkClient = customerBulkClient;
    }

    public ImportSummary importCustomers(MultipartFile file, int batchSize) {
        ImportSummary summary = new ImportSummary();
        List<BulkUpsertItem> batch = new ArrayList<BulkUpsertItem>(batchSize);
        File tempFile = null;

        try {
            tempFile = Files.createTempFile("cmp-import-", ".xlsx").toFile();
            try (InputStream inputStream = file.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            }

            OPCPackage opcPackage = OPCPackage.open(tempFile);
            XSSFReader xssfReader = new XSSFReader(opcPackage);
            StylesTable styles = xssfReader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opcPackage);
            DataFormatter dataFormatter = new DataFormatter(Locale.US);

            XSSFReader.SheetIterator iterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            if (!iterator.hasNext()) {
                throw new IllegalArgumentException("Excel file is empty");
            }

            InputStream sheet = iterator.next();
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(new XSSFSheetXMLHandler(styles, null, strings,
                    new ImportSheetHandler(batch, summary, batchSize), dataFormatter, false));
            parser.parse(new InputSource(sheet));
            sheet.close();
            opcPackage.close();

            flushBatch(batch, summary);
            return summary;
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to process import file", ex);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private void flushBatch(List<BulkUpsertItem> batch, ImportSummary summary) {
        if (batch.isEmpty()) {
            return;
        }
        BulkUpsertResponse response = customerBulkClient.send(new BulkUpsertRequest(new ArrayList<BulkUpsertItem>(batch)));
        if (response != null) {
            summary.addCreated(response.getCreatedCount());
            summary.addUpdated(response.getUpdatedCount());
        }
        batch.clear();
    }

    private class ImportSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private final List<BulkUpsertItem> batch;
        private final ImportSummary summary;
        private final int batchSize;
        private final List<String> currentRow = new ArrayList<String>();

        private ImportSheetHandler(List<BulkUpsertItem> batch, ImportSummary summary, int batchSize) {
            this.batch = batch;
            this.summary = summary;
            this.batchSize = batchSize;
        }

        @Override
        public void startRow(int rowNum) {
            currentRow.clear();
        }

        @Override
        public void endRow(int rowNum) {
            if (rowNum == 0) {
                return;
            }
            summary.incrementRead();
            String name = getCell(NAME_COL);
            String dob = getCell(DOB_COL);
            String nic = getCell(NIC_COL);
            if (isBlank(name) || isBlank(dob) || isBlank(nic)) {
                summary.incrementRejected();
                return;
            }
            LocalDate parsedDate = parseDate(dob);
            if (parsedDate == null) {
                summary.incrementRejected();
                return;
            }
            batch.add(new BulkUpsertItem(name.trim(), parsedDate, nic.trim()));
            summary.incrementAccepted();
            if (batch.size() >= batchSize) {
                flushBatch(batch, summary);
            }
        }

        @Override
        public void cell(String cellReference, String formattedValue, org.apache.poi.xssf.usermodel.XSSFComment comment) {
            int column = getColumnIndex(cellReference);
            while (currentRow.size() <= column) {
                currentRow.add("");
            }
            currentRow.set(column, formattedValue == null ? "" : formattedValue);
        }

        @Override
        public void headerFooter(String text, boolean isHeader, String tagName) {
            // no-op
        }

        private String getCell(int index) {
            return currentRow.size() > index ? currentRow.get(index) : "";
        }

        private boolean isBlank(String value) {
            return value == null || value.trim().isEmpty();
        }

        private int getColumnIndex(String cellRef) {
            int index = 0;
            for (int i = 0; i < cellRef.length(); i++) {
                char c = cellRef.charAt(i);
                if (Character.isDigit(c)) {
                    break;
                }
                index = index * 26 + (Character.toUpperCase(c) - 'A' + 1);
            }
            return index - 1;
        }

        private LocalDate parseDate(String value) {
            String trimmed = value.trim();
            try {
                return LocalDate.parse(trimmed, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException ex) {
                try {
                    return LocalDate.parse(trimmed, DateTimeFormatter.ofPattern("M/d/yyyy"));
                } catch (DateTimeParseException ignored) {
                    return null;
                }
            }
        }
    }
}


