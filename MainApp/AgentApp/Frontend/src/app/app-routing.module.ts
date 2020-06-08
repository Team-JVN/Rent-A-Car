
import { ResetPasswordEnterNewPassComponent } from './component/authentification/reset-password-enter-new-pass/reset-password-enter-new-pass.component';
import { ResetPasswordEnterEmailComponent } from './component/authentification/reset-password-enter-email/reset-password-enter-email.component';
import { ActivateAccountComponent } from './component/authentification/activate-account/activate-account.component';
import { ManageRentReportsGuard } from './guard/manage.rent.reports.guard';
import { ManageRoleGuard } from './guard/manage.role.guard';
import { ManageClientsGuard } from './guard/manage.clients.guard';
import { ManagePriceListsGuard } from './guard/manage.price.lists.guard';
import { ManageCarsGuard } from './guard/manage.cars.guard';
import { ManageCodeBooksGuard } from './guard/manage.code.books.guard';
import { ManageAdvertisemetsGuard } from './guard/manage.advertisements.guard';
import { EditRoleComponent } from './component/edit/edit-role/edit-role.component';
import { ClientRentRequestDetailsComponent } from './component/details/client-rent-request-details/client-rent-request-details.component';
import { RentRequestDetailsComponent } from './component/details/rent-request-details/rent-request-details.component';
import { NonAuthenticatedErrorPageComponent } from './error/non-authenticated-error-page/non-authenticated-error-page.component';
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
import { ListRentReportsComponent } from './component/list/list-rent-reports/list-rent-reports.component';
import { MyRentRequestsGuard } from './guard/my.rent.requests.guard';
import { ListClientRentRequestsComponent } from './component/list/list-client-rent-requests/list-client-rent-requests.component';
import { SearchAdvertisementsComponent } from './component/list/search-advertisements/search-advertisements.component';
import { RentingCartComponent } from './component/renting-cart/renting-cart.component';
import { ClientEditProfileGuard } from './guard/client.edit.profile';
import { EditAgentComponent } from './component/edit/editPersonalInfo/edit-agent/edit-agent.component';
import { AgentEditProfileGuard } from './guard/agent.edit.profile';
import { EditClientComponent } from './component/edit/editPersonalInfo/edit-client/edit-client.component';
import { GetStatisticsGuard } from './guard/get.statistics.guard';
import { CarsStatisticsComponent } from './component/list/cars-statistics/cars-statistics.component';

const routes: Routes = [
  /** MANAGE ADVERTISEMENTS */
  {
    path: "advertisements",
    component: ListAdvertisementsComponent,
    canActivate: [ManageAdvertisemetsGuard],
  },
  {
    path: 'rent-requests/:id',
    component: ListRentRequestsComponent,
    canActivate: [ManageAdvertisemetsGuard],
  },
  {
    path: 'rent-request/:id',
    component: RentRequestDetailsComponent,
    canActivate: [ManageAdvertisemetsGuard],
  },
  /** MANAGE CODE BOOKS */
  {
    path: "body-styles",
    component: ListBodyStylesComponent,
    canActivate: [ManageCodeBooksGuard],
  },
  {
    path: "fuel-types",
    component: ListFuelTypeComponent,
    canActivate: [ManageCodeBooksGuard],
  },
  {
    path: "gearbox-types",
    component: ListGearBoxTypesComponent,
    canActivate: [ManageCodeBooksGuard],
  },
  {
    path: "makes",
    component: ListMakesComponent,
    canActivate: [ManageCodeBooksGuard],
  },
  {
    path: "models/:id",
    component: ListModelsComponent,
    canActivate: [ManageCodeBooksGuard],
  },
  /** MANAGE CARS */
  {
    path: "cars",
    component: ListCarsComponent,
    canActivate: [ManageCarsGuard],
  },
  /** GET STATISTICS */
  {
    path: 'statistics',
    component: CarsStatisticsComponent,
    canActivate: [GetStatisticsGuard],
  },
  /** MANAGE PRICE LISTS */
  {
    path: 'price-lists',
    component: ListPriceListsComponent,
    canActivate: [ManagePriceListsGuard],
  },
  /** MANAGE CLIENTS */
  {
    path: "clients",
    component: ListClientsComponent,
    canActivate: [ManageClientsGuard],
  },

  /** Get my rent requests */
  {
    path: 'client-rent-request/:id',
    component: ClientRentRequestDetailsComponent,
    canActivate: [MyRentRequestsGuard],
  },
  {
    path: 'client/renting-cart',
    component: RentingCartComponent,
    canActivate: [MyRentRequestsGuard],
  },
  {
    path: 'client-rent-requests',
    component: ListClientRentRequestsComponent,
    canActivate: [MyRentRequestsGuard],
  },
  /** MANAGE RENT REPORTS */
  {
    path: "rent-reports",
    component: ListRentReportsComponent,
    canActivate: [ManageRentReportsGuard],
  },
  /** MANAGE ROLES */
  {
    path: "role",
    component: EditRoleComponent,
    canActivate: [ManageRoleGuard],
  },
  /**EDIT CLIENT INFO */
  {
    path: "edit-client-info",
    component: EditClientComponent,
    canActivate: [ClientEditProfileGuard],
  },
  /**EDIT AGENT INFO */
  {
    path: "edit-agent-info",
    component: EditAgentComponent,
    canActivate: [AgentEditProfileGuard],
  },
  /** Unauthorized user */
  {
    path: "login",
    component: LoginComponent,
  },
  {
    path: "change-password",
    component: ChangePasswordComponent,
  },
  {
    path: 'client/registration',
    component: ClientRegistrationComponent
  },
  {
    path: "advertisement/:id",
    component: AdvertisementDetailsComponent,
  },
  {
    path: "activate-account",
    component: ActivateAccountComponent,
  },
  {
    path: "forgot-password",
    component: ResetPasswordEnterEmailComponent,
  },
  {
    path: "reset-password",
    component: ResetPasswordEnterNewPassComponent,
  },
  {
    path: 'search-advertisements',
    component: SearchAdvertisementsComponent
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
    path: "",
    component: SearchAdvertisementsComponent,
  },
  {
    path: "**",
    component: SearchAdvertisementsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
