import { HttpClient, HttpParams } from '@angular/common/http';
import { Subject } from 'rxjs';
import { Client } from './../model/client';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  url = environment.baseUrl + environment.client;
  updateSuccessEmitter = new Subject<Client>();
  createSuccessEmitter = new Subject<Client>();
  rejectSuccessEmitter = new Subject<Client>();
  deleteSuccessEmitter = new Subject<Client>();

  constructor(private httpClient: HttpClient) { }

  public create(client: Client): any {
    return this.httpClient.post(this.url, client);
  }

  public edit(client: Client): any {
    return this.httpClient.put(this.url, client);
  }

  public approve(id: number) {
    return this.httpClient.put(this.url + '/' + id + '/approve', null);
  }

  public getClientsForRentRequest() {
    return this.httpClient.get(this.url + '/for-rent-request');
  }

  public getLoggedInUser() {
    return this.httpClient.get(this.url + '/logged-in-user');
  }
  public getAll(status: string) {
    return this.httpClient.get(this.url + '/all/' + status);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }

  public block(id: number): any {
    return this.httpClient.put(this.url + '/' + id + '/block', null);
  }

  public unblock(id: number): any {
    return this.httpClient.put(this.url + '/' + id + '/unblock', null);
  }

  public activateAccount(token: string) {
    let params = new HttpParams();
    params = params.append('t', token);
    return this.httpClient.put(this.url + '/activate', {}, { params: params });
  }

  public reject(id: number, reason: String) {
    return this.httpClient.put(this.url + '/' + id + '/reject', reason);
  }

  public creatingRentRequests(id: number, status: string) {
    return this.httpClient.put(this.url + '/' + id + '/create-rent-requests/' + status, null);
  }

  public creatingComments(id: number, status: string) {
    return this.httpClient.put(this.url + '/' + id + '/create-comments/' + status, null);
  }

}
