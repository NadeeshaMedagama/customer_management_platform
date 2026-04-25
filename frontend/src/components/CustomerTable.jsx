import React from 'react';

export default function CustomerTable({ data, onSelect }) {
  return (
    <div className="card">
      <h3>Customers</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>DOB</th>
            <th>NIC</th>
            <th>Mobile Count</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {data.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.name}</td>
              <td>{c.dateOfBirth}</td>
              <td>{c.nicNumber}</td>
              <td>{(c.mobileNumbers || []).length}</td>
              <td><button className="ghost" onClick={() => onSelect(c.id)}>View</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

