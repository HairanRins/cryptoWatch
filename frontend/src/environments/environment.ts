export const environment = {
  production: import.meta.env.PROD,
  apiUrl: import.meta.env.NG_APP_API_URL || 'http://localhost:8080/api',
};
