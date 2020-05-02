import { SearchAdvertisementsComponent } from './component/list/search-advertisements/search-advertisements.component';
import { NonAuthenticatedErrorPageComponent } from './error/non-authenticated-error-page/non-authenticated-error-page.component';
import { ClientPendingApprovalComponent } from './component/authentification/client-pending-approval/client-pending-approval.component';
import { ClientRegistrationComponent } from './component/authentification/client-registration/client-registration.component';
import { ChangePasswordComponent } from './component/authentification/change-password/change-password.component';
import { LoginComponent } from './component/authentification/login/login.component';
import { ListRentRequestsComponent } from './component/list/list-rent-requests/list-rent-requests.component';
import { ListClientsComponent } from './component/list/list-clients/list-clients.component';
import { AdvertisementDetailsComponent } from './component/advertisement-details/advertisement-details.component';
import { ListCarsComponent } from './component/list/list-cars/list-cars.component';
import { ListGearBoxTypesComponent } from './component/list/list-gear-box-types/list-gear-box-types.component';
import { ListFuelTypeComponent } from './component/list/list-fuel-type/list-fuel-type.component';
import { ListBodyStylesComponent } from './component/list/list-body-styles/list-body-styles.component';
import { ListPriceListsComponent } from './component/list/list-price-lists/list-price-lists.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ListAdvertisementsComponent } from './component/list/list-advertisements/list-advertisements.component';
import { NonAuthorizedErrorPageComponent } from './error/non-authorized-error-page/non-authorized-error-page.component';
import { ListMakesComponent } from './component/list/list-makes/list-makes.component';
import { ListModelsComponent } from './component/list/list-models/list-models.component';

const routes: Routes = [
  {
    path: 'advertisement/:id',
    component: AdvertisementDetailsComponent
  },
  {
    path: 'body-styles',
    component: ListBodyStylesComponent
  },
  {
    path: 'fuel-types',
    component: ListFuelTypeComponent
  },
  {
    path: 'gearbox-types',
    component: ListGearBoxTypesComponent
  },
  {
    path: 'cars',
    component: ListCarsComponent
  },
  {
    path: 'price-lists',
    component: ListPriceListsComponent
  },
  {
    path: 'clients',
    component: ListClientsComponent,
  },
  {
    path: 'rent-requests',
    component: ListRentRequestsComponent,
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'change-password',
    component: ChangePasswordComponent
  },
  {
    path: 'search-advertisements',
    component: SearchAdvertisementsComponent
  },
  {
    path: 'client/registration',
    component: ClientRegistrationComponent
  },
  {
    path: 'client/pending-approval',
    component: ClientPendingApprovalComponent
  },
  {
    path: "makes",
    component: ListMakesComponent,
  },
  {
    path: "models/:id",
    component: ListModelsComponent,
  },
  //******************* ERROR PAGES ************************
  {
    path: 'error/non-authenticated',
    component: NonAuthenticatedErrorPageComponent,
  },
  {
    path: 'error/non-authorized',
    component: NonAuthorizedErrorPageComponent
  },
  {
    path: '',
    component: ListAdvertisementsComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
