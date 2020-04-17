import { Advertisement } from './../model/advertisement';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
@Injectable({
  providedIn: 'root'
})
export class AdvertisementService {
  url = environment.baseUrl + environment.advertisement;
  updateSuccessEmitter = new Subject<Advertisement>();
  createSuccessEmitter = new Subject<Advertisement>();

  constructor(private httpClient: HttpClient) { }

  public create(advertisement: Advertisement): any {
    return this.httpClient.post(this.url, advertisement);
  }

  public edit(advertisement: Advertisement): any {
    return this.httpClient.put(this.url + '/' + advertisement.id, advertisement);
  }

  public getAdvertisements() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }
}
