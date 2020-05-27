// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  // baseUrl: 'https://localhost:8090',
  baseUrl: 'http://localhost:8090',
  advertisement: '/api/advertisement',
  bodyStyle: '/api/body-style',
  fuelType: '/api/fuel-type',
  gearBoxType: '/api/gearbox-type',
  car: '/api/car',
  priceList: '/api/price-list',
  client: '/api/client',
  rentRequest: '/api/rent-request',
  auth: '/api/auth',
  agent: '/api/agent',
  admin: '/api/admin',
  make: "/api/make",
  model: "/api/model",
  message: "/api/message",
  comment: '/api/comment',
  rentReport: "/api/rent-report",
  role: "/api/role",
  permission: "/api/permission",
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
