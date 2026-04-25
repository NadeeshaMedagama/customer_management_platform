import React, { useEffect, useState } from 'react';

const emptyAddress = { addressLine1: '', addressLine2: '', cityCode: '', countryCode: '' };

const defaultForm = {
  name: '',
  dateOfBirth: '',
  nicNumber: '',
  mobileNumbers: [''],
  familyMemberNics: [''],
  addresses: [{ ...emptyAddress }]
};

export default function CustomerForm({ onSubmit, loading, initialData, onClearSelection }) {
  const [form, setForm] = useState(defaultForm);

  useEffect(() => {
    if (!initialData) {
      setForm(defaultForm);
      return;
    }
    setForm({
      name: initialData.name || '',
      dateOfBirth: initialData.dateOfBirth || '',
      nicNumber: initialData.nicNumber || '',
      mobileNumbers: initialData.mobileNumbers?.length ? initialData.mobileNumbers : [''],
      familyMemberNics: initialData.familyMemberNics?.length ? initialData.familyMemberNics : [''],
      addresses: initialData.addresses?.length
        ? initialData.addresses.map((a) => ({
          addressLine1: a.addressLine1 || '',
          addressLine2: a.addressLine2 || '',
          cityCode: a.cityCode || '',
          countryCode: a.countryCode || ''
        }))
        : [{ ...emptyAddress }]
    });
  }, [initialData]);

  const updateList = (field, idx, value) => {
    const next = [...form[field]];
    next[idx] = value;
    setForm({ ...form, [field]: next });
  };

  const updateAddress = (idx, key, value) => {
    const next = [...form.addresses];
    next[idx] = { ...next[idx], [key]: value };
    setForm({ ...form, addresses: next });
  };

  const submit = (e) => {
    e.preventDefault();
    const payload = {
      ...form,
      mobileNumbers: form.mobileNumbers.filter(Boolean),
      familyMemberNics: form.familyMemberNics.filter(Boolean),
      addresses: form.addresses.filter((a) => a.addressLine1 || a.addressLine2 || a.cityCode || a.countryCode)
    };
    onSubmit(payload, initialData?.id);
  };

  return (
    <form className="card" onSubmit={submit}>
      <h3>{initialData ? `Update Customer #${initialData.id}` : 'Create Customer'}</h3>
      <div className="grid-3">
        <input placeholder="Name*" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
        <input type="date" value={form.dateOfBirth} onChange={(e) => setForm({ ...form, dateOfBirth: e.target.value })} required />
        <input placeholder="NIC*" value={form.nicNumber} onChange={(e) => setForm({ ...form, nicNumber: e.target.value })} required />
      </div>

      <label>Mobile Numbers</label>
      {form.mobileNumbers.map((num, idx) => (
        <input key={`m-${idx}`} value={num} onChange={(e) => updateList('mobileNumbers', idx, e.target.value)} placeholder="e.g. +94770000000" />
      ))}
      <button type="button" className="ghost" onClick={() => setForm({ ...form, mobileNumbers: [...form.mobileNumbers, ''] })}>+ Add Mobile</button>

      <label>Family Member NICs</label>
      {form.familyMemberNics.map((nic, idx) => (
        <input key={`f-${idx}`} value={nic} onChange={(e) => updateList('familyMemberNics', idx, e.target.value)} placeholder="Existing family member NIC" />
      ))}
      <button type="button" className="ghost" onClick={() => setForm({ ...form, familyMemberNics: [...form.familyMemberNics, ''] })}>+ Add Family Member NIC</button>

      <label>Addresses</label>
      {form.addresses.map((address, idx) => (
        <div className="grid-4" key={`a-${idx}`}>
          <input placeholder="Address line 1" value={address.addressLine1} onChange={(e) => updateAddress(idx, 'addressLine1', e.target.value)} />
          <input placeholder="Address line 2" value={address.addressLine2} onChange={(e) => updateAddress(idx, 'addressLine2', e.target.value)} />
          <input placeholder="City code (CMB)" value={address.cityCode} onChange={(e) => updateAddress(idx, 'cityCode', e.target.value)} />
          <input placeholder="Country code (LK)" value={address.countryCode} onChange={(e) => updateAddress(idx, 'countryCode', e.target.value)} />
        </div>
      ))}
      <button type="button" className="ghost" onClick={() => setForm({ ...form, addresses: [...form.addresses, { ...emptyAddress }] })}>+ Add Address</button>

      <button type="submit" disabled={loading}>{loading ? 'Saving...' : initialData ? 'Update Customer' : 'Create Customer'}</button>
      {initialData && (
        <button type="button" className="ghost" onClick={onClearSelection}>Clear Selection</button>
      )}
    </form>
  );
}


