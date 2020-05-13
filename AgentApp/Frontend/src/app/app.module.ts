import { ListRentReportsComponent } from './component/list/list-rent-reports/list-rent-reports.component';
import { AgentGuard } from './guard/agent.guard';
import { TokenInterceptor } from './interceptor/token.interceptor';
import { AddRentRequestComponent } from './component/add/add-rent-request/add-rent-request.component';
import { HeaderComponent } from './component/header/header.component';
import { CustomHammerConfig } from './custom-hummer-config';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MaterialModule } from './material-module';
import { BrowserModule, HAMMER_GESTURE_CONFIG } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LayoutModule } from '@angular/cdk/layout';
import { ToastrModule } from 'ngx-toastr';
import { NgxGalleryModule } from 'ngx-gallery';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ListBodyStylesComponent } from './component/list/list-body-styles/list-body-styles.component';
import { AddBodyStyleComponent } from './component/add/add-body-style/add-body-style.component';
import { EditBodyStyleComponent } from './component/edit/edit-body-style-component/edit-body-style-component.component';
import { EditFuelTypeComponent } from './component/edit/edit-fuel-type/edit-fuel-type.component';
import { ListFuelTypeComponent } from './component/list/list-fuel-type/list-fuel-type.component';
import { AddFuelTypeComponent } from './component/add/add-fuel-type/add-fuel-type.component';
import { ListGearBoxTypesComponent } from './component/list/list-gear-box-types/list-gear-box-types.component';
import { AddGearBoxTypeComponent } from './component/add/add-gear-box-type/add-gear-box-type.component';
import { EditGearBoxTypeComponent } from './component/edit/edit-gear-box-type/edit-gear-box-type.component';
import { ListCarsComponent } from './component/list/list-cars/list-cars.component';
import { EditCarComponent } from './component/edit/edit-car/edit-car.component';
import { AddCarComponent } from './component/add/add-car/add-car.component';
import { NgxDropzoneModule } from 'ngx-dropzone';
import { EditCarPartialComponent } from './component/edit/edit-car-partial/edit-car-partial.component';
import { AddAdvertisementComponent } from './component/add/add-advertisement/add-advertisement.component';
import { ListAdvertisementsComponent } from './component/list/list-advertisements/list-advertisements.component';
import { ListPriceListsComponent } from './component/list/list-price-lists/list-price-lists.component';
import { AddPriceListComponent } from './component/add/add-price-list/add-price-list.component';
import { EditPriceListComponent } from './component/edit/edit-price-list/edit-price-list.component';
import { ViewPicturesComponent } from './component/view-pictures/view-pictures.component';
import { EditAdvertisementComponent } from './component/edit/edit-advertisement/edit-advertisement.component';
import { ListClientsComponent } from './component/list/list-clients/list-clients.component';
import { AddClientComponent } from './component/add/add-client/add-client.component';
import { EditClientComponent } from './component/edit/edit-client/edit-client.component';
import { AdvertisementDetailsComponent } from './component/advertisement-details/advertisement-details.component';
import { ListRentRequestsComponent } from './component/list/list-rent-requests/list-rent-requests.component';
import { LoginComponent } from './component/authentification/login/login.component';
import { ChangePasswordComponent } from './component/authentification/change-password/change-password.component';
import { ClientRegistrationComponent } from './component/authentification/client-registration/client-registration.component';
import { NonAuthorizedErrorPageComponent } from './error/non-authorized-error-page/non-authorized-error-page.component';
import { NonAuthenticatedErrorPageComponent } from './error/non-authenticated-error-page/non-authenticated-error-page.component';
import { AddModelComponent } from './component/add/add-model/add-model.component';
import { EditModelComponent } from './component/edit/edit-model/edit-model.component';
import { EditMakeComponent } from './component/edit/edit-make/edit-make.component';
import { AddMakeComponent } from './component/add/add-make/add-make.component';
import { ListMakesComponent } from './component/list/list-makes/list-makes.component';
import { ListModelsComponent } from './component/list/list-models/list-models.component';
import { ConfirmDialogDeleteMakeComponent } from './component/confirm-dialog/confirm-dialog-delete-make/confirm-dialog-delete-make.component';
import { RatingModule } from 'ng-starrating';
import { RentRequestDetailsComponent } from './component/details/rent-request-details/rent-request-details.component';
import { ClientRentRequestDetailsComponent } from './component/details/client-rent-request-details/client-rent-request-details.component';
import { ClientGuard } from './guard/client.guard';
import { EditAdvertisementPartialComponent } from './component/edit/edit-advertisement-partial/edit-advertisement-partial.component';

@NgModule({
  declarations: [
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
    ListClientsComponent,
    AddClientComponent,
    EditClientComponent,
    AddRentRequestComponent,
    AdvertisementDetailsComponent,
    ListRentRequestsComponent,
    LoginComponent,
    ChangePasswordComponent,
    ClientRegistrationComponent,
    NonAuthorizedErrorPageComponent,
    NonAuthenticatedErrorPageComponent,
    AddModelComponent,
    AddMakeComponent,
    EditModelComponent,
    EditMakeComponent,
    ListMakesComponent,
    ListModelsComponent,
    ConfirmDialogDeleteMakeComponent,
    RentRequestDetailsComponent,
    ClientRentRequestDetailsComponent,
    EditAdvertisementPartialComponent,
    ListRentReportsComponent,
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
    AddMakeComponent,
    EditModelComponent,
    EditMakeComponent,
    AddModelComponent,
  ],
  providers: [
    {
      provide: HAMMER_GESTURE_CONFIG, useClass: CustomHammerConfig
    },
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true },
    // { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    AgentGuard,
    ClientGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
