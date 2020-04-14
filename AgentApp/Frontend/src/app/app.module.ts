import { HeaderComponent } from './component/header/header.component';
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
import { ListPriceListsComponent } from './component/list/list-price-lists/list-price-lists.component';
import { AddPriceListComponent } from './component/add/add-price-list/add-price-list.component';
import { EditPriceListComponent } from './component/edit/edit-price-list/edit-price-list.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ListPriceListsComponent,
    AddPriceListComponent,
    EditPriceListComponent
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
  ],
  entryComponents: [
    AddPriceListComponent,
    EditPriceListComponent,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
