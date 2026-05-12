const fs = require('fs');
const path = require('path');

const apiUrl = process.env.NG_APP_API_URL || 'http://localhost:8080/api';

const content = `export const environment = {
  production: true,
  apiUrl: '${apiUrl}',
};
`;

fs.writeFileSync(path.join(__dirname, '..', 'src', 'environments', 'environment.prod.ts'), content);
console.log(`✓ environment.prod.ts generated with apiUrl: ${apiUrl}`);
