import { Router } from '@angular/router';
import { PriceList } from './../model/priceList';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PriceListService {
  url = environment.baseUrl + environment.priceList;
  createSuccessEmitter = new Subject<PriceList>();

  constructor(private httpClient: HttpClient, private router: Router) { }

  public create(priceList: PriceList) {
    return this.httpClient.post(this.url, priceList);
  }

  public getAll() {
    return this.httpClient.get(this.url);
  }

}
