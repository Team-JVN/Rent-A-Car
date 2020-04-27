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

  constructor(private httpClient: HttpClient) { }

  public add(admin: Admin): any {
    return this.httpClient.post(this.url, admin);
  }

  public edit(admin: Admin): any {
    return this.httpClient.put(this.url + '/' + admin.id, admin);
  }

  public getAdmins() {
    return this.httpClient.get(this.url);
  }

  public delete(id: number): any {
    return this.httpClient.delete(this.url + '/' + id);
  }
}
