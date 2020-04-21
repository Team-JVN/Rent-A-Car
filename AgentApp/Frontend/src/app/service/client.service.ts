import { HttpClient } from '@angular/common/http';
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

  constructor(private httpClient: HttpClient) { }

  public create(client: Client): any {
    return this.httpClient.post(this.url, client);
  }

  public edit(client: Client): any {
    return this.httpClient.put(this.url + '/' + client.id, client);
  }

  public getClients() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }

}
