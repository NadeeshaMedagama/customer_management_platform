import React, { useEffect, useState } from 'react';
import { Sun, Moon, CheckCircle2, AlertCircle } from 'lucide-react';
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
  const [message, setMessage] = useState({ text: '', type: '' });

  const fetchCustomers = async () => {
    try {
      const res = await customerApi.get('/api/customers?page=0&size=20');
      setCustomers(res.data.content || []);
    } catch {
      showMessage('Failed to load customers', 'error');
    }
  };

  const showMessage = (text, type = 'success') => {
    setMessage({ text, type });
    setTimeout(() => setMessage({ text: '', type: '' }), 5000);
  };

  useEffect(() => {
    fetchCustomers();
  }, []);

  const handleSave = async (payload, selectedId) => {
    setLoading(true);
    try {
      if (selectedId) {
        await customerApi.put(`/api/customers/${selectedId}`, payload);
      } else {
        await customerApi.post('/api/customers', payload);
      }
      await fetchCustomers();
      setSelectedCustomer(null); // Return to default
      showMessage(selectedId ? 'Customer updated successfully' : 'Customer created successfully');
    } catch (err) {
      showMessage(err?.response?.data?.message || 'Save failed', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleSelect = async (id) => {
    try {
      const res = await customerApi.get(`/api/customers/${id}`);
      setSelectedCustomer(res.data);
    } catch (err) {
      showMessage(err?.response?.data?.message || 'Customer fetch failed', 'error');
    }
  };

  const handleUpload = async (file) => {
    setImportLoading(true);
    try {
      const body = new FormData();
      body.append('file', file);
      const res = await importApi.post('/api/import/customers?batchSize=1000', body);
      setImportResult(res.data);
      await fetchCustomers();
      showMessage('Bulk import completed');
    } catch (err) {
      showMessage(err?.response?.data?.message || 'Import failed', 'error');
    } finally {
      setImportLoading(false);
    }
  };

  return (
    <div className={dark ? 'theme-dark app' : 'theme-light app'}>
      <div className="container">
        <header className="app-header">
          <div className="title-group">
            <h1>Customer Management</h1>
            <p className="subtitle">Professional customer dashboard</p>
          </div>
          <button
            className="theme-toggle"
            onClick={() => setDark(!dark)}
            aria-label="Toggle Theme"
          >
            {dark ? <Sun size={24} /> : <Moon size={24} />}
          </button>
        </header>

        {message.text && (
          <div className={`banner banner-${message.type}`}>
            {message.type === 'error' ? <AlertCircle size={20} /> : <CheckCircle2 size={20} />}
            <span>{message.text}</span>
          </div>
        )}

        <div className="dashboard-layout">
          <div className="main-content">
            <div className="card">
              <div className="card-header">
                <h2>Customer Directory</h2>
              </div>
              <CustomerTable data={customers} onSelect={handleSelect} />
            </div>

            {selectedCustomer && (
              <div className="card details-card mt-4">
                <div className="card-header">
                  <h2>Customer Record: {selectedCustomer.name}</h2>
                </div>
                <div className="details-content">
                  <div className="detail-row"><strong>NIC:</strong> {selectedCustomer.nicNumber}</div>
                  <div className="detail-row"><strong>DOB:</strong> {selectedCustomer.dateOfBirth}</div>
                  <div className="detail-row"><strong>Mobiles:</strong> {selectedCustomer.mobileNumbers?.join(', ') || 'N/A'}</div>

                  {selectedCustomer.addresses?.length > 0 && (
                    <div className="address-section">
                      <h4>Addresses:</h4>
                      {selectedCustomer.addresses.map((a, i) => (
                        <div key={i} className="address-block">
                          {a.addressLine1}, {a.addressLine2 && `${a.addressLine2}, `}
                          {a.city} ({a.cityCode}), {a.country}
                        </div>
                      ))}
                    </div>
                  )}
                  {selectedCustomer.familyMembers?.length > 0 && (
                    <div className="family-section">
                      <h4>Family Members:</h4>
                      <div className="family-tags">
                        {selectedCustomer.familyMembers.map((f, i) => (
                          <span key={i} className="badge">{f.nicNumber}</span>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>

          <div className="side-panel">
            <CustomerForm
              onSubmit={handleSave}
              loading={loading}
              initialData={selectedCustomer}
              onClearSelection={() => setSelectedCustomer(null)}
            />
            <div className="mt-4">
              <BulkImportPanel onUpload={handleUpload} loading={importLoading} result={importResult} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}


