// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  // baseUrl: 'https://localhost:8080',
  baseUrl: 'http://localhost:8080',
  advertisement: '/advertisements/api/advertisement',
  bodyStyle: '/cars/api/body-style',
  fuelType: '/cars/api/fuel-type',
  gearBoxType: '/cars/api/gearbox-type',
  car: '/cars/api/car',
  priceList: '/advertisements/api/price-list',
  client: '/users/api/client',
  rentRequest: '/renting/api/rent-request',
  auth: '/users/api/auth',
  agent: '/users/api/agent',
  admin: '/users/api/admin',
  make: "/cars/api/make",
  model: "/cars/api/model",
  message: "/renting/api/message",
  comment: '/renting/api/comment',
  rentReport: "/renting/api/rent-report",
  role: "/users/api/role",
  permission: "/users/api/permission",
  searchAdvertisements: "/search/api/advertisement"
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
