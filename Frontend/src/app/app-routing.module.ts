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


const routes: Routes = [
  {
    path: '',
    component: ListAdvertisementsComponent
  },
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
    path: '',
    component: ListAdvertisementsComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
