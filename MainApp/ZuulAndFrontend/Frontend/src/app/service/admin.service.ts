import { Subject } from 'rxjs';
import { environment } from './../../environments/environment';
import { Admin } from './../model/admin';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  url = environment.baseUrl + environment.admin;
  createSuccessEmitter = new Subject<Admin>();
  deleteSuccessEmitter = new Subject<Admin>();

  constructor(private httpClient: HttpClient) { }

  public add(admin: Admin): any {
    return this.httpClient.post(this.url, admin);
  }

  public edit(admin: Admin): any {
    return this.httpClient.put(this.url, admin);
  }

  public getAll(status: string) {
    return this.httpClient.get(this.url + '/all/' + status);
  }

  public getLoggedInUser() {
    return this.httpClient.get(this.url + '/logged-in-user');
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }
}
