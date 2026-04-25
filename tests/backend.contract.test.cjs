const fs = require('node:fs');
const path = require('node:path');
const yaml = require('js-yaml');

const repoRoot = path.resolve(__dirname, '..');

function read(fileRelativePath) {
  return fs.readFileSync(path.join(repoRoot, fileRelativePath), 'utf8');
}

describe('backend contract safeguards', () => {
  const openApi = yaml.load(read('docs/openapi.yaml'));
  const schemaSql = read('database/ddl/01_schema.sql');
  const compose = read('docker-compose.yml');

  test('exposes the customer and import API paths', () => {
    expect(openApi.paths).toHaveProperty('/api/customers');
    expect(openApi.paths).toHaveProperty('/api/customers/{id}');
    expect(openApi.paths).toHaveProperty('/internal/customers/bulk-upsert');
    expect(openApi.paths).toHaveProperty('/api/import/customers');
  });

  test('requires core customer fields in request payload', () => {
    const required = openApi.components.schemas.CustomerRequest.required;
    expect(required).toEqual(expect.arrayContaining(['name', 'dateOfBirth', 'nicNumber']));
  });

  test('keeps import batch size constrained for stability', () => {
    const batchSize = openApi.paths['/api/import/customers'].post.parameters.find(
      (parameter) => parameter.name === 'batchSize'
    );

    expect(batchSize.schema.minimum).toBe(100);
    expect(batchSize.schema.maximum).toBe(5000);
    expect(batchSize.schema.default).toBe(1000);
  });

  test('enforces unique NIC and mandatory customer identity columns', () => {
    expect(schemaSql).toMatch(/nic_number\s+VARCHAR\(50\)\s+NOT\s+NULL\s+UNIQUE/i);
    expect(schemaSql).toMatch(/name\s+VARCHAR\(200\)\s+NOT\s+NULL/i);
    expect(schemaSql).toMatch(/date_of_birth\s+DATE\s+NOT\s+NULL/i);
  });

  test('protects family links with composite key and cascading references', () => {
    expect(schemaSql).toMatch(/PRIMARY\s+KEY\s*\(customer_id,\s*family_member_id\)/i);
    expect(schemaSql).toMatch(/FOREIGN\s+KEY\s*\(family_member_id\)\s+REFERENCES\s+customers\(id\)\s+ON\s+DELETE\s+CASCADE/i);
  });

  test('keeps city and country master data codes unique', () => {
    expect(schemaSql).toMatch(/CREATE\s+TABLE\s+IF\s+NOT\s+EXISTS\s+countries[\s\S]*?code\s+VARCHAR\(10\)\s+NOT\s+NULL\s+UNIQUE/i);
    expect(schemaSql).toMatch(/CREATE\s+TABLE\s+IF\s+NOT\s+EXISTS\s+cities[\s\S]*?code\s+VARCHAR\(30\)\s+NOT\s+NULL\s+UNIQUE/i);
  });

  test('wires import-service to customer-service internal bulk endpoint', () => {
    expect(compose).toMatch(/import-service:[\s\S]*depends_on:[\s\S]*customer-service:[\s\S]*condition:\s*service_started/i);
    expect(compose).toMatch(/CUSTOMER_SERVICE_BULK_URL:\s*http:\/\/customer-service:8081\/internal\/customers\/bulk-upsert/i);
  });
});

