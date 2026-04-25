package com.cmp.importsvc.service;

import com.cmp.importsvc.dto.BulkUpsertRequest;
import com.cmp.importsvc.dto.BulkUpsertResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomerBulkClient {

    private final RestTemplate restTemplate;

    @Value("${customer.service.internal-url:http://localhost:8081/internal/customers/bulk-upsert}")
    private String bulkUpsertUrl;

    public CustomerBulkClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BulkUpsertResponse send(BulkUpsertRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BulkUpsertRequest> entity = new HttpEntity<BulkUpsertRequest>(request, headers);
        return restTemplate.postForObject(bulkUpsertUrl, entity, BulkUpsertResponse.class);
    }
}

