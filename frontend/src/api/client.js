import axios from 'axios';

export const customerApi = axios.create({
  baseURL: import.meta.env.VITE_CUSTOMER_API_URL || 'http://localhost:8081'
});

export const importApi = axios.create({
  baseURL: import.meta.env.VITE_IMPORT_API_URL || 'http://localhost:8082'
});

