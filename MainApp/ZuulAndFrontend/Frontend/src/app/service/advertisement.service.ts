import { AdvertisementEdit } from './../model/advertisementEdit';
import { Advertisement } from './../model/advertisement';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { CreateAdvertisement } from '../model/createAdvertisement';
import { AdvertisementEditAllInfo } from '../model/advertisement.edit.all.info';

@Injectable({
  providedIn: 'root'
})
export class AdvertisementService {
  url = environment.baseUrl + environment.advertisement;
  updateSuccessEmitter = new Subject<Advertisement>();
  createSuccessEmitter = new Subject<Advertisement>();

  constructor(private httpClient: HttpClient) { }

  public create(advertisement: CreateAdvertisement): any {
    return this.httpClient.post(this.url, advertisement);
  }

  public edit(advertisement: AdvertisementEditAllInfo): any {
    return this.httpClient.put(this.url + '/' + advertisement.id, advertisement);
  }

  public editPartial(advertisement: AdvertisementEdit, id: number): any {
    return this.httpClient.put(this.url + '/' + id + '/partial', advertisement);
  }

  public getAll(status: string) {
    return this.httpClient.get(this.url + '/all/' + status);
  }

  public get(id: number) {
    return this.httpClient.get(this.url + "/" + id);
  }

  public getRentRequests(advertisementId: number, status: string) {
    return this.httpClient.get(this.url + "/" + advertisementId + "/rent-requests/" + status);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }

  public getCarEditType(id: number) {
    return this.httpClient.get(this.url + '/car/' + id + '/edit-type');
  }

  public getCarLocation(advId: number) {
    return this.httpClient.get(this.url + "/" + advId + '/location');
  }
}
