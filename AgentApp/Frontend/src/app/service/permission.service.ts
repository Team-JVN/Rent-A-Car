import { HttpClient } from '@angular/common/http';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PermissionService {
  url = environment.baseUrl + environment.permission;

  constructor(private httpClient: HttpClient) { }

  public getPermissions() {
    return this.httpClient.get(this.url);
  }

}
