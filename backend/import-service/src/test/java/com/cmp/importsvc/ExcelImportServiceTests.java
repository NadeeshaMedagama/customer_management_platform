package com.cmp.importsvc;

import com.cmp.importsvc.dto.BulkUpsertRequest;
import com.cmp.importsvc.dto.BulkUpsertResponse;
import com.cmp.importsvc.dto.ImportSummary;
import com.cmp.importsvc.service.CustomerBulkClient;
import com.cmp.importsvc.service.ExcelImportService;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExcelImportServiceTests {

    @Test
    void shouldParseAndAggregateRows() throws Exception {
        CustomerBulkClient fakeClient = new CustomerBulkClient(null) {
            @Override
            public BulkUpsertResponse send(BulkUpsertRequest request) {
                BulkUpsertResponse response = new BulkUpsertResponse();
                try {
                    java.lang.reflect.Field created = BulkUpsertResponse.class.getDeclaredField("createdCount");
                    created.setAccessible(true);
                    created.set(response, request.getCustomers().size());
                    java.lang.reflect.Field updated = BulkUpsertResponse.class.getDeclaredField("updatedCount");
                    updated.setAccessible(true);
                    updated.set(response, 0);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                return response;
            }
        };

        ExcelImportService service = new ExcelImportService(fakeClient);
        MockMultipartFile file = new MockMultipartFile("file", "customers.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", workbookBytes());

        ImportSummary summary = service.importCustomers(file, 2);

        assertEquals(3, summary.getRowsRead());
        assertEquals(2, summary.getRowsAccepted());
        assertEquals(1, summary.getRowsRejected());
        assertEquals(2, summary.getCreated());
    }

    private byte[] workbookBytes() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("customers");
        sheet.createRow(0).createCell(0).setCellValue("name");
        sheet.getRow(0).createCell(1).setCellValue("date_of_birth");
        sheet.getRow(0).createCell(2).setCellValue("nic_number");

        sheet.createRow(1).createCell(0).setCellValue("John");
        sheet.getRow(1).createCell(1).setCellValue("1990-01-01");
        sheet.getRow(1).createCell(2).setCellValue("NIC-1");

        sheet.createRow(2).createCell(0).setCellValue("Jane");
        sheet.getRow(2).createCell(1).setCellValue("1988-02-03");
        sheet.getRow(2).createCell(2).setCellValue("NIC-2");

        sheet.createRow(3).createCell(0).setCellValue("Broken");
        sheet.getRow(3).createCell(1).setCellValue("bad-date");
        sheet.getRow(3).createCell(2).setCellValue("NIC-3");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}

