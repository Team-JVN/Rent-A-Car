import { RentingCartComponent } from './component/renting-cart/renting-cart.component';
import { CarsStatisticsComponent } from './component/list/cars-statistics/cars-statistics.component';
import { SearchAdvertisementsComponent } from './component/list/search-advertisements/search-advertisements.component';
import { ListClientRentRequestsComponent } from './component/list/list-client-rent-requests/list-client-rent-requests.component';
import { ClientRentRequestDetailsComponent } from './component/details/client-rent-request-details/client-rent-request-details.component';
import { RentRequestDetailsComponent } from './component/details/rent-request-details/rent-request-details.component';
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
import { ManageUsersComponent } from "./component/manage-users/manage-users.component";
import { EditClientInfoComponent } from "./component/edit/edit-personal-info/edit-client/edit-client-info.component";
import { EditAgentComponent } from "./component/edit/edit-personal-info/edit-agent/edit-agent.component";
import { EditAdminComponent } from "./component/edit/edit-personal-info/edit-admin/edit-admin.component";
import { ResetPasswordEnterNewPassComponent } from './component/authentification/reset-password-enter-new-pass/reset-password-enter-new-pass.component';
import { ResetPasswordEnterEmailComponent } from './component/authentification/reset-password-enter-email/reset-password-enter-email.component';
import { EditRoleComponent } from './component/edit/edit-role/edit-role.component';
import { ActivateAccountComponent } from './component/authentification/activate-account/activate-account.component';

const routes: Routes = [
  {
    path: "advertisement/:id",
    component: AdvertisementDetailsComponent,
  },
  {
    path: "activate-account",
    component: ActivateAccountComponent,
  },
  {
    path: "body-styles",
    component: ListBodyStylesComponent,
  },
  {
    path: "fuel-types",
    component: ListFuelTypeComponent,
  },
  {
    path: "gearbox-types",
    component: ListGearBoxTypesComponent,
  },
  {
    path: "cars",
    component: ListCarsComponent,
  },
  {
    path: 'statistics',
    component: CarsStatisticsComponent
  },
  {
    path: 'price-lists',
    component: ListPriceListsComponent
  },
  {
    path: "clients",
    component: ListClientsComponent,
  },
  {
    path: 'rent-requests/:id',
    component: ListRentRequestsComponent,
  },
  {
    path: 'client-rent-requests',
    component: ListClientRentRequestsComponent,
  },
  {
    path: 'rent-request/:id',
    component: RentRequestDetailsComponent
  },
  {
    path: 'client-rent-request/:id',
    component: ClientRentRequestDetailsComponent
  },
  {
    path: "reset-password",
    component: ResetPasswordEnterNewPassComponent,
  },
  {
    path: "forgot-password",
    component: ResetPasswordEnterEmailComponent,
  },
  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: "change-password",
    component: ChangePasswordComponent,
  },
  {
    path: "client/registration",
    component: ClientRegistrationComponent,
  },
  {
    path: "client/pending-approval",
    component: ClientPendingApprovalComponent,
  },
  {
    path: "manage-users",
    component: ManageUsersComponent,
  },
  {
    path: "edit-client-info",
    component: EditClientInfoComponent,
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
    path: "edit-agent-info",
    component: EditAgentComponent,
  },
  {
    path: "edit-admin-info",
    component: EditAdminComponent,
  },
  {
    path: 'client/renting-cart',
    component: RentingCartComponent
  },
  {
    path: "makes",
    component: ListMakesComponent,
  },
  {
    path: "models/:id",
    component: ListModelsComponent,
  },
  /** MANAGE ROLES */
  {
    path: "role",
    component: EditRoleComponent,
  },
  //******************* ERROR PAGES ************************
  {
    path: "error/non-authenticated",
    component: NonAuthenticatedErrorPageComponent,
  },
  {
    path: "error/non-authorized",
    component: NonAuthorizedErrorPageComponent,
  },
  {
    path: "advertisements",
    component: ListAdvertisementsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
