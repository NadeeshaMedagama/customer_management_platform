# Documentation Guide

This folder contains API and integration documentation for `customer_management_platform`.

## Contents

- `docs/openapi.yaml`
  - Full machine-readable API contract (OpenAPI 3.0.3).
- `docs/api-contract.md`
  - Human-friendly API contract with endpoint behavior, examples, and error models.
- `docs/postman/customer_management_platform.postman_collection.json`
  - Ready-to-import Postman collection.
- `docs/postman/customer_management_platform.postman_environment.json`
  - Local environment variables for Postman.

## Quick Start (Postman)

1. Import collection file:
   - `docs/postman/customer_management_platform.postman_collection.json`
2. Import environment file:
   - `docs/postman/customer_management_platform.postman_environment.json`
3. Select environment `customer_management_platform-local`.
4. Run requests in this order:
   1. `Create Customer`
   2. `Get Customer By ID`
   3. `Update Customer`
   4. `List Customers`
   5. `Import Customers (Excel)`

## Notes

- Internal endpoint folder is marked as internal use.
- Import request requires an `.xlsx` file selected in Postman form-data.
- `customer_id` is auto-updated by tests after successful create response.

