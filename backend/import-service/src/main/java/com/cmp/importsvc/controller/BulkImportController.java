package com.cmp.importsvc.controller;

import com.cmp.importsvc.dto.ImportSummary;
import com.cmp.importsvc.service.ExcelImportService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@Validated
public class BulkImportController {

    private final ExcelImportService excelImportService;

    public BulkImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping(path = "/customers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportSummary importCustomers(@RequestPart("file") MultipartFile file,
                                         @RequestParam(defaultValue = "1000") int batchSize) {
        int safeBatchSize = Math.max(100, Math.min(batchSize, 5000));
        return excelImportService.importCustomers(file, safeBatchSize);
    }
}

