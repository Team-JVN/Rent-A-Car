import { GetStatisticsGuard } from "./guard/get.statistics.guard";
import { MyRentRequestsGuard } from "./guard/my.rent.requests.guard";
import { ManagePriceListsGuard } from "./guard/manage.pricelists.guard";
import { ManageAdvertisementsGuard } from "./guard/manage.advertisements.guard";
import { ManageCodeBooksGuard } from "./guard/manage.code.books.guard";
import { ManageCarsGuard } from "./guard/manage.cars.guard";
import { ClientEditProfileGuard } from "./guard/client.edit.profile.guard";
import { AgentEditProfileGuard } from "./guard/agent.edit.profile.guard";
import { AdminEditProfileGuard } from "./guard/admin.edit.profile.guard";
import { ManageUsersGuard } from "./guard/manage.users.guard";
import { ManageRoleGuard } from "./guard/manage.role.guard";

import { TokenInterceptor } from "./interceptor/token.interceptor";
import { AddRentRequestComponent } from "./component/add/add-rent-request/add-rent-request.component";
import { AddRentReportComponent } from "./component/add/add-rent-report/add-rent-report.component";
import { HeaderComponent } from "./component/header/header.component";
import { CustomHammerConfig } from "./custom-hummer-config";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MaterialModule } from "./material-module";
import {
  BrowserModule,
  HAMMER_GESTURE_CONFIG,
} from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ReactiveFormsModule, FormsModule } from "@angular/forms";
import { LayoutModule } from "@angular/cdk/layout";
import { ToastrModule } from "ngx-toastr";
import { NgxGalleryModule } from "ngx-gallery";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { ListBodyStylesComponent } from "./component/list/list-body-styles/list-body-styles.component";
import { AddBodyStyleComponent } from "./component/add/add-body-style/add-body-style.component";
import { EditBodyStyleComponent } from "./component/edit/edit-body-style-component/edit-body-style-component.component";
import { EditFuelTypeComponent } from "./component/edit/edit-fuel-type/edit-fuel-type.component";
import { ListFuelTypeComponent } from "./component/list/list-fuel-type/list-fuel-type.component";
import { AddFuelTypeComponent } from "./component/add/add-fuel-type/add-fuel-type.component";
import { ListGearBoxTypesComponent } from "./component/list/list-gear-box-types/list-gear-box-types.component";
import { AddGearBoxTypeComponent } from "./component/add/add-gear-box-type/add-gear-box-type.component";
import { EditGearBoxTypeComponent } from "./component/edit/edit-gear-box-type/edit-gear-box-type.component";
import { ListCarsComponent } from "./component/list/list-cars/list-cars.component";
import { EditCarComponent } from "./component/edit/edit-car/edit-car.component";
import { AddCarComponent } from "./component/add/add-car/add-car.component";
import { NgxDropzoneModule } from "ngx-dropzone";
import { EditCarPartialComponent } from "./component/edit/edit-car-partial/edit-car-partial.component";
import { AddAdvertisementComponent } from "./component/add/add-advertisement/add-advertisement.component";
import { ListAdvertisementsComponent } from "./component/list/list-advertisements/list-advertisements.component";
import { ListPriceListsComponent } from "./component/list/list-price-lists/list-price-lists.component";
import { AddPriceListComponent } from "./component/add/add-price-list/add-price-list.component";
import { EditPriceListComponent } from "./component/edit/edit-price-list/edit-price-list.component";
import { ViewPicturesComponent } from "./component/view-pictures/view-pictures.component";
import { EditAdvertisementComponent } from "./component/edit/edit-advertisement/edit-advertisement.component";
import { AddClientComponent } from "./component/add/add-client/add-client.component";
import { EditClientComponent } from "./component/edit/edit-client/edit-client.component";
import { AdvertisementDetailsComponent } from "./component/advertisement-details/advertisement-details.component";
import { ListRentRequestsComponent } from "./component/list/list-rent-requests/list-rent-requests.component";
import { LoginComponent } from "./component/authentification/login/login.component";
import { ChangePasswordComponent } from "./component/authentification/change-password/change-password.component";
import { ClientRegistrationComponent } from "./component/authentification/client-registration/client-registration.component";
import { ClientPendingApprovalComponent } from "./component/authentification/client-pending-approval/client-pending-approval.component";
import { AddAgentComponent } from "./component/add/add-agent/add-agent.component";
import { AddAdminComponent } from "./component/add/add-admin/add-admin.component";
import { NonAuthorizedErrorPageComponent } from "./error/non-authorized-error-page/non-authorized-error-page.component";
import { NonAuthenticatedErrorPageComponent } from "./error/non-authenticated-error-page/non-authenticated-error-page.component";
import { AddModelComponent } from "./component/add/add-model/add-model.component";
import { EditModelComponent } from "./component/edit/edit-model/edit-model.component";
import { EditMakeComponent } from "./component/edit/edit-make/edit-make.component";
import { AddMakeComponent } from "./component/add/add-make/add-make.component";
import { ListMakesComponent } from "./component/list/list-makes/list-makes.component";
import { ListComments } from "./component/list/list-comments/list-comments.component";
import { ListModelsComponent } from "./component/list/list-models/list-models.component";
import { ConfirmDialogDeleteMakeComponent } from "./component/confirm-dialog/confirm-dialog-delete-make/confirm-dialog-delete-make.component";
import { SearchAdvertisementsComponent } from "./component/list/search-advertisements/search-advertisements.component";
import { RatingModule } from "ng-starrating";
import { RentRequestDetailsComponent } from "./component/details/rent-request-details/rent-request-details.component";
import { RentReportDetailsComponent } from "./component/details/rent-report-details/rent-report-details.component";
import { ViewMessagesComponent } from "./component/view-messages/view-messages.component";
import { ReviewFeedbackComponent } from "./component/review-feedback/review-feedback.component";
import { ListClientRentRequestsComponent } from "./component/list/list-client-rent-requests/list-client-rent-requests.component";
import { ClientRentRequestDetailsComponent } from "./component/details/client-rent-request-details/client-rent-request-details.component";
import { LeaveFeedbackComponent } from "./component/add/leave-feedback/leave-feedback.component";
import { RentingCartComponent } from "./component/renting-cart/renting-cart.component";
import { CarsStatisticsComponent } from "./component/list/cars-statistics/cars-statistics.component";
import { TableForStatisticsComponent } from "./component/list/table-for-statistics/table-for-statistics.component";
import { EditAdvertisementPartialComponent } from "./component/edit/edit-advertisement-partial/edit-advertisement-partial.component";
import { ManageUsersComponent } from "./component/manage-users/manage-users.component";
import { ManageCommentsComponent } from "./component/manage-comments/manage-comments.component";
import { EditClientInfoComponent } from "./component/edit/edit-personal-info/edit-client/edit-client-info.component";
import { EditAgentComponent } from "./component/edit/edit-personal-info/edit-agent/edit-agent.component";
import { EditAdminComponent } from "./component/edit/edit-personal-info/edit-admin/edit-admin.component";
import { ActivateAccountComponent } from "./component/authentification/activate-account/activate-account.component";
import { ResetPasswordEnterEmailComponent } from "./component/authentification/reset-password-enter-email/reset-password-enter-email.component";
import { ResetPasswordEnterNewPassComponent } from "./component/authentification/reset-password-enter-new-pass/reset-password-enter-new-pass.component";
import { ErrorInterceptor } from "./interceptor/error.interceptor";
import { EditRoleComponent } from "./component/edit/edit-role/edit-role.component";
import { ViewPermissionsComponent } from "./component/view/view-permissions/view-permissions.component";
import { ReplacePipe } from "./util/replace.pipe";
import { RejectRequestToRegisterComponent } from "./component/reject-request-to-register/reject-request-to-register.component";
import { ConfirmDeleteClientComponent } from "./component/confirm-dialog/confirm-delete-client/confirm-delete-client.component";
import { ConfirmDialogDeleteAgentComponent } from "./component/confirm-dialog/confirm-dialog-delete-agent/confirm-dialog-delete-agent.component";
import { ConfirmDialogDeleteAdminComponent } from "./component/confirm-dialog/confirm-dialog-delete-admin/confirm-dialog-delete-admin.component";
import { AddToCartComponent } from "./component/add/add-to-cart/add-to-cart.component";
import { ManageCommentsGuard } from "./guard/manage.coments.guard";

@NgModule({
  declarations: [
    ReplacePipe,
    AppComponent,
    ListBodyStylesComponent,
    HeaderComponent,
    AddBodyStyleComponent,
    EditBodyStyleComponent,
    EditFuelTypeComponent,
    ListFuelTypeComponent,
    AddFuelTypeComponent,
    ListGearBoxTypesComponent,
    AddGearBoxTypeComponent,
    EditGearBoxTypeComponent,
    ListCarsComponent,
    EditCarComponent,
    AddCarComponent,
    EditCarPartialComponent,
    AddAdvertisementComponent,
    ListAdvertisementsComponent,
    HeaderComponent,
    ListPriceListsComponent,
    AddPriceListComponent,
    EditPriceListComponent,
    ViewPicturesComponent,
    EditAdvertisementComponent,
    AddClientComponent,
    EditClientComponent,
    AddRentRequestComponent,
    AddRentReportComponent,
    AdvertisementDetailsComponent,
    ListRentRequestsComponent,
    LoginComponent,
    ChangePasswordComponent,
    ClientRegistrationComponent,
    ClientPendingApprovalComponent,
    AddAgentComponent,
    AddAdminComponent,
    NonAuthorizedErrorPageComponent,
    NonAuthenticatedErrorPageComponent,
    AddModelComponent,
    AddMakeComponent,
    EditModelComponent,
    EditMakeComponent,
    ListMakesComponent,
    ListComments,
    ListModelsComponent,
    ConfirmDialogDeleteMakeComponent,
    SearchAdvertisementsComponent,
    RentRequestDetailsComponent,
    RentReportDetailsComponent,
    ViewMessagesComponent,
    ReviewFeedbackComponent,
    ListClientRentRequestsComponent,
    ClientRentRequestDetailsComponent,
    LeaveFeedbackComponent,
    RentingCartComponent,
    CarsStatisticsComponent,
    TableForStatisticsComponent,
    EditAdvertisementPartialComponent,
    ManageUsersComponent,
    ManageCommentsComponent,
    EditClientInfoComponent,
    EditAgentComponent,
    EditAdminComponent,
    ActivateAccountComponent,
    ResetPasswordEnterEmailComponent,
    ResetPasswordEnterNewPassComponent,
    EditRoleComponent,
    ViewPermissionsComponent,
    RejectRequestToRegisterComponent,
    ConfirmDeleteClientComponent,
    ConfirmDialogDeleteAgentComponent,
    ConfirmDialogDeleteAdminComponent,
    AddToCartComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    FormsModule,
    LayoutModule,
    ToastrModule.forRoot({
      timeOut: 3000,
      positionClass: "toast-top-right",
      preventDuplicates: true,
    }),
    AppRoutingModule,
    MaterialModule,
    HttpClientModule,
    MatDatepickerModule,
    NgxDropzoneModule,
    NgxGalleryModule,
    RatingModule,
  ],
  entryComponents: [
    AddBodyStyleComponent,
    EditBodyStyleComponent,
    EditFuelTypeComponent,
    AddFuelTypeComponent,
    AddGearBoxTypeComponent,
    EditGearBoxTypeComponent,
    EditCarComponent,
    AddCarComponent,
    EditCarPartialComponent,
    AddAdvertisementComponent,
    AddPriceListComponent,
    EditPriceListComponent,
    ViewPicturesComponent,
    EditAdvertisementComponent,
    EditAdvertisementPartialComponent,
    AddClientComponent,
    EditClientComponent,
    AddRentRequestComponent,
    AddRentReportComponent,
    RentReportDetailsComponent,
    AddAgentComponent,
    AddAdminComponent,
    AddMakeComponent,
    AddModelComponent,
    ConfirmDialogDeleteMakeComponent,
    EditModelComponent,
    EditMakeComponent,
    ListComments,
    ReviewFeedbackComponent,
    LeaveFeedbackComponent,
    EditClientInfoComponent,
    EditAdminComponent,
    AddModelComponent,
    ConfirmDeleteClientComponent,
    RejectRequestToRegisterComponent,
    ConfirmDialogDeleteAgentComponent,
    ConfirmDialogDeleteAdminComponent,
    AddToCartComponent,
  ],
  providers: [
    {
      provide: HAMMER_GESTURE_CONFIG,
      useClass: CustomHammerConfig,
    },
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    ManageRoleGuard,
    ManageUsersGuard,
    AdminEditProfileGuard,
    AgentEditProfileGuard,
    ClientEditProfileGuard,
    ManageCarsGuard,
    GetStatisticsGuard,
    ManageCodeBooksGuard,
    ManageAdvertisementsGuard,
    ManagePriceListsGuard,
    MyRentRequestsGuard,
    ManageCommentsGuard,
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
