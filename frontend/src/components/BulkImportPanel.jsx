import React, { useState } from 'react';

export default function BulkImportPanel({ onUpload, loading, result }) {
  const [file, setFile] = useState(null);

  return (
    <div className="card">
      <h3>Bulk Customer Create / Update</h3>
      <p className="hint">Upload `.xlsx` with columns: name, date_of_birth (yyyy-MM-dd), nic_number.</p>
      <input type="file" accept=".xlsx" onChange={(e) => setFile(e.target.files?.[0] || null)} />
      <button disabled={!file || loading} onClick={() => onUpload(file)}>
        {loading ? 'Uploading...' : 'Upload Excel'}
      </button>
      {result && (
        <div className="summary">
          <span>Rows Read: {result.rowsRead}</span>
          <span>Accepted: {result.rowsAccepted}</span>
          <span>Rejected: {result.rowsRejected}</span>
          <span>Created: {result.created}</span>
          <span>Updated: {result.updated}</span>
        </div>
      )}
    </div>
  );
}

