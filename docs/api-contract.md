# API Contract - Customer Management Platform

This document is the human-readable API contract for all currently implemented endpoints.

- Customer service base URL: `http://localhost:8081`
- Import service base URL: `http://localhost:8082`
- Machine-readable contract: `docs/openapi.yaml`

## Conventions

- Content type: `application/json` for JSON endpoints.
- Date format: `yyyy-MM-dd`.
- `nicNumber` is unique system-wide.
- City and country are referenced by master codes (`cityCode`, `countryCode`).

## 1) Customer Service (Public)

### 1.1 Create Customer

- Method: `POST`
- Path: `/api/customers`
- Request body schema: `CustomerRequest`
- Success response: `200 OK`, `CustomerResponse`
- Error response: `400 Bad Request`, `CustomerError`

Sample request:

```json
{
  "name": "Nadeesha",
  "dateOfBirth": "1995-06-20",
  "nicNumber": "NIC-778899",
  "mobileNumbers": ["+94771234567", "+94770000000"],
  "familyMemberNics": ["NIC-123456"],
  "addresses": [
    {
      "addressLine1": "No 12, Main Street",
      "addressLine2": "Ward Place",
      "cityCode": "CMB",
      "countryCode": "LK"
    }
  ]
}
```

### 1.2 Update Customer

- Method: `PUT`
- Path: `/api/customers/{id}`
- Path params:
  - `id` (`Long`, required)
- Request body schema: `CustomerRequest`
- Success response: `200 OK`, `CustomerResponse`
- Error responses:
  - `400 Bad Request` (`CustomerError`)
  - `404 Not Found` (`CustomerError`)

### 1.3 Get Customer by ID

- Method: `GET`
- Path: `/api/customers/{id}`
- Path params:
  - `id` (`Long`, required)
- Success response: `200 OK`, `CustomerResponse`
- Error response: `404 Not Found`, `CustomerError`

### 1.4 List Customers (Paginated)

- Method: `GET`
- Path: `/api/customers`
- Query params:
  - `page` (`int`, optional, default `0`)
  - `size` (`int`, optional, default `20`, max effective value `200`)
- Success response: `200 OK`, `Page<CustomerResponse>`

## 2) Customer Service (Internal)

### 2.1 Bulk Upsert Customers

- Method: `POST`
- Path: `/internal/customers/bulk-upsert`
- Intended caller: `import-service`
- Request body schema: `BulkUpsertRequest`
- Success response: `200 OK`, `BulkUpsertResponse`
- Error response: `400 Bad Request`, `CustomerError`

Sample request:

```json
{
  "customers": [
    {
      "name": "John Doe",
      "dateOfBirth": "1990-01-10",
      "nicNumber": "NIC-10001"
    },
    {
      "name": "Jane Doe",
      "dateOfBirth": "1992-07-18",
      "nicNumber": "NIC-10002"
    }
  ]
}
```

Sample response:

```json
{
  "createdCount": 1,
  "updatedCount": 1
}
```

## 3) Import Service (Public)

### 3.1 Import Customers from Excel

- Method: `POST`
- Path: `/api/import/customers`
- Content type: `multipart/form-data`
- Form fields:
  - `file` (`.xlsx`, required)
- Query params:
  - `batchSize` (`int`, optional, default `1000`)
  - Service clamps values to `100..5000`
- Success response: `200 OK`, `ImportSummary`
- Error responses:
  - `400 Bad Request`, `ImportError`
  - `500 Internal Server Error`, `ImportError`

Expected columns in first sheet:

1. `name`
2. `date_of_birth` (`yyyy-MM-dd` or `M/d/yyyy`)
3. `nic_number`

Sample success response:

```json
{
  "rowsRead": 1000,
  "rowsAccepted": 997,
  "rowsRejected": 3,
  "created": 900,
  "updated": 97
}
```

## 4) Schema Definitions

### 4.1 CustomerRequest

| Field | Type | Required | Notes |
|---|---|---|---|
| `name` | string | yes | must be non-blank |
| `dateOfBirth` | string(date) | yes | ISO date |
| `nicNumber` | string | yes | unique |
| `mobileNumbers` | string[] | no | optional list |
| `familyMemberNics` | string[] | no | existing customer NICs |
| `addresses` | AddressRequest[] | no | optional list |

### 4.2 AddressRequest

| Field | Type | Required | Notes |
|---|---|---|---|
| `addressLine1` | string | no | |
| `addressLine2` | string | no | |
| `cityCode` | string | no | master city code |
| `countryCode` | string | no | master country code |

### 4.3 CustomerResponse

| Field | Type |
|---|---|
| `id` | long |
| `name` | string |
| `dateOfBirth` | string(date) |
| `nicNumber` | string |
| `mobileNumbers` | string[] |
| `familyMemberNics` | string[] |
| `addresses` | AddressResponse[] |

### 4.4 AddressResponse

| Field | Type |
|---|---|
| `addressLine1` | string |
| `addressLine2` | string |
| `cityCode` | string |
| `city` | string |
| `countryCode` | string |
| `country` | string |

### 4.5 BulkUpsertRequest

| Field | Type | Required |
|---|---|---|
| `customers` | BulkUpsertItem[] | yes, non-empty |

`BulkUpsertItem` fields:

- `name` (required)
- `dateOfBirth` (required, date)
- `nicNumber` (required)

### 4.6 BulkUpsertResponse

| Field | Type |
|---|---|
| `createdCount` | int |
| `updatedCount` | int |

### 4.7 ImportSummary

| Field | Type |
|---|---|
| `rowsRead` | long |
| `rowsAccepted` | long |
| `rowsRejected` | long |
| `created` | long |
| `updated` | long |

## 5) Error Models

### 5.1 CustomerError

Returned by `customer-service` handlers:

```json
{
  "timestamp": "2026-04-23T14:08:00Z",
  "status": 400,
  "message": "Validation failed"
}
```

### 5.2 ImportError

Returned by `import-service` handlers:

```json
{
  "message": "Failed to process import file"
}
```

## 6) Behavior Rules

- NIC uniqueness is enforced.
- Family members must already exist as customers.
- A customer cannot reference itself as a family member.
- Address `cityCode` and `countryCode` must exist in master tables.
- Bulk import performs create/update by NIC match.

