import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RentRequest } from '../model/rentRequest';

@Injectable({
  providedIn: 'root'
})
export class RentRequestService {
  url = environment.baseUrl + environment.rentRequest;
  updateSuccessEmitter = new Subject<RentRequest>();
  createSuccessEmitter = new Subject<RentRequest>();

  constructor(private httpClient: HttpClient) { }

  public create(rentRequest: RentRequest): any {
    return this.httpClient.post(this.url, rentRequest);
  }

  public edit(rentRequest: RentRequest): any {
    return this.httpClient.put(this.url + '/' + rentRequest.id, rentRequest);
  }

  public getClients() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }
}
