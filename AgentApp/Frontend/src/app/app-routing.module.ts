import { ListCarsComponent } from './component/list/list-cars/list-cars.component';
import { ListGearBoxTypesComponent } from './component/list/list-gear-box-types/list-gear-box-types.component';
import { ListFuelTypeComponent } from './component/list/list-fuel-type/list-fuel-type.component';
import { ListBodyStylesComponent } from './component/list/list-body-styles/list-body-styles.component';
import { ListPriceListsComponent } from './component/list/list-price-lists/list-price-lists.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ListAdvertisementsComponent } from './component/list/list-advertisements/list-advertisements.component';


const routes: Routes = [
  {
    path: 'body-style',
    component: ListBodyStylesComponent,
  },
  {
    path: 'fuel-type',
    component: ListFuelTypeComponent,
  },
  {
    path: 'gearbox-type',
    component: ListGearBoxTypesComponent,
  },
  {
    path: 'car',
    component: ListCarsComponent,
  },
  {
    path: 'advertisement',
    component: ListAdvertisementsComponent
  },
  {
    path: 'price-lists',
    component: ListPriceListsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
