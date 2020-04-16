import { MatDatepickerModule } from '@angular/material/datepicker';
import { MaterialModule } from './material-module';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { LayoutModule } from '@angular/cdk/layout';
import { ToastrModule } from 'ngx-toastr';
import { HttpClientModule } from '@angular/common/http';
import { ListBodyStylesComponent } from './component/list/list-body-styles/list-body-styles.component';
import { HeaderComponent } from './component/header/header.component';
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
    EditCarPartialComponent
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
