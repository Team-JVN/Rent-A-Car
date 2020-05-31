import { Role } from './../model/role';
import { HttpClient } from '@angular/common/http';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  url = environment.baseUrl + environment.role;

  constructor(private httpClient: HttpClient) { }

  public getRoles() {
    return this.httpClient.get(this.url);
  }

  public getPermissions(roleId: number) {
    return this.httpClient.get(this.url + '/' + roleId + '/permission');
  }

  public edit(role: Role): any {
    return this.httpClient.put(this.url + '/' + role.id, role);
  }
}
