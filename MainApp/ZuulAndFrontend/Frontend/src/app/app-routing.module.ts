import { AdminEditProfileGuard } from './guard/admin.edit.profile.guard';
import { AgentEditProfileGuard } from './guard/agent.edit.profile.guard';
import { ClientEditProfileGuard } from './guard/client.edit.profile.guard';
import { ManageRoleGuard } from './guard/manage.role.guard';
import { MyRentRequestsGuard } from './guard/my.rent.requests.guard';
import { ManageUsersGuard } from './guard/manage.users.guard';
import { ManagePriceListsGuard } from './guard/manage.pricelists.guard';
import { ManageCarsGuard } from './guard/manage.cars.guard';
import { ManageCodeBooksGuard } from './guard/manage.code.books.guard';
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
import { ManageAdvertisementsGuard } from './guard/manage.advertisements.guard';

const routes: Routes = [
  /** MANAGE ADVERTISEMENTS */
  {
    path: "advertisements",
    component: ListAdvertisementsComponent,
    canActivate: [ManageAdvertisementsGuard],
  },
  /** Get received rent requests */
  {
    path: 'rent-requests/:id',
    component: ListRentRequestsComponent,
    canActivate: [ManageAdvertisementsGuard],
  },
  {
    path: 'rent-request/:id',
    component: RentRequestDetailsComponent,
    canActivate: [ManageAdvertisementsGuard],
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
  /** MANAGE PRICE LISTS */
  {
    path: 'price-lists',
    component: ListPriceListsComponent,
    canActivate: [ManagePriceListsGuard],
  },

  /** MANAGE USERS */
  {
    path: "manage-users",
    component: ManageUsersComponent,
    canActivate: [ManageUsersGuard]
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
    path: 'statistics',
    component: CarsStatisticsComponent
  },
  {
    path: "client/pending-approval",
    component: ClientPendingApprovalComponent,
  },
  {
    path: 'search-advertisements',
    component: SearchAdvertisementsComponent
  },
  /**EDIT CLIENT INFO */
  {
    path: "edit-client-info",
    component: EditClientInfoComponent,
    canActivate: [ClientEditProfileGuard],
  },
  /**EDIT AGENT INFO */
  {
    path: "edit-agent-info",
    component: EditAgentComponent,
    canActivate: [AgentEditProfileGuard],
  },
  /**EDIT ADMIN INFO */
  {
    path: "edit-admin-info",
    component: EditAdminComponent,
    canActivate: [AdminEditProfileGuard],
  },
  /** MANAGE ROLES */
  {
    path: "role",
    component: EditRoleComponent,
    canActivate: [ManageRoleGuard],
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
    component: SearchAdvertisementsComponent, //set search page
  },
  {
    path: '**',
    component: SearchAdvertisementsComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule { }
