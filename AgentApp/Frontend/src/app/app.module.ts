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
import { HttpClientModule } from '@angular/common/http';
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
import { EditRentRequestComponent } from './component/edit/edit-rent-request/edit-rent-request.component';

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
    EditRentRequestComponent,
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
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),
    AppRoutingModule,
    MaterialModule,
    HttpClientModule,
    MatDatepickerModule,
    NgxDropzoneModule,
    NgxGalleryModule,
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
    AddClientComponent,
    EditClientComponent,
    AddRentRequestComponent,
    EditRentRequestComponent
  ],
  providers: [
    {
      provide: HAMMER_GESTURE_CONFIG, useClass: CustomHammerConfig
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
