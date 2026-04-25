import React, { useEffect, useState } from 'react';
import CustomerForm from './components/CustomerForm';
import CustomerTable from './components/CustomerTable';
import BulkImportPanel from './components/BulkImportPanel';
import { customerApi, importApi } from './api/client';

export default function App() {
  const [dark, setDark] = useState(true);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [importLoading, setImportLoading] = useState(false);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [importResult, setImportResult] = useState(null);
  const [message, setMessage] = useState('');

  const fetchCustomers = async () => {
    const res = await customerApi.get('/api/customers?page=0&size=20');
    setCustomers(res.data.content || []);
  };

  useEffect(() => {
    fetchCustomers().catch(() => setMessage('Failed to load customers'));
  }, []);

  const handleSave = async (payload, selectedId) => {
    setLoading(true);
    setMessage('');
    try {
      if (selectedId) {
        await customerApi.put(`/api/customers/${selectedId}`, payload);
      } else {
        await customerApi.post('/api/customers', payload);
      }
      await fetchCustomers();
      setMessage(selectedId ? 'Customer updated successfully' : 'Customer created successfully');
    } catch (err) {
      setMessage(err?.response?.data?.message || 'Save failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSelect = async (id) => {
    try {
      const res = await customerApi.get(`/api/customers/${id}`);
      setSelectedCustomer(res.data);
    } catch (err) {
      setMessage(err?.response?.data?.message || 'Customer fetch failed');
    }
  };

  const handleUpload = async (file) => {
    setImportLoading(true);
    setMessage('');
    try {
      const body = new FormData();
      body.append('file', file);
      const res = await importApi.post('/api/import/customers?batchSize=1000', body);
      setImportResult(res.data);
      await fetchCustomers();
      setMessage('Bulk import completed');
    } catch (err) {
      setMessage(err?.response?.data?.message || 'Import failed');
    } finally {
      setImportLoading(false);
    }
  };

  return (
    <div className={dark ? 'theme-dark app' : 'theme-light app'}>
      <header>
        <div>
          <h1>Customer Management Platform</h1>
          <p>Professional customer management dashboard</p>
        </div>
        <button className="ghost" onClick={() => setDark(!dark)}>
          {dark ? 'Switch to Light' : 'Switch to Dark'}
        </button>
      </header>

      {message && <div className="banner">{message}</div>}

      <div className="layout">
        <CustomerForm
          onSubmit={handleSave}
          loading={loading}
          initialData={selectedCustomer}
          onClearSelection={() => setSelectedCustomer(null)}
        />
        <BulkImportPanel onUpload={handleUpload} loading={importLoading} result={importResult} />
      </div>

      <CustomerTable data={customers} onSelect={handleSelect} />

      {selectedCustomer && (
        <div className="card">
          <h3>Customer Details</h3>
          <pre>{JSON.stringify(selectedCustomer, null, 2)}</pre>
        </div>
      )}
    </div>
  );
}


