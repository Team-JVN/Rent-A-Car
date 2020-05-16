import { UserTokenState } from './../model/userTokenState';

import { ChangePassword } from './../model/changePassword';
import { map } from 'rxjs/operators';
import { User } from './../model/user';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Router } from '@angular/router';
import { RegistrationClient } from '../model/registrationClient';
import { Permission } from '../model/permission';

@Injectable({
  providedIn: 'root'
})
export class AuthentificationService {

  url = environment.baseUrl + environment.auth;
  access_token = null;
  userTokenStateSubject: BehaviorSubject<UserTokenState>;
  userTokenState: Observable<UserTokenState>;
  role: String;
  permissions: Permission[] = [];
  loggedInUserEmail: string;
  refreshToken = null;

  constructor(private httpClient: HttpClient, private router: Router) {
    this.userTokenStateSubject = new BehaviorSubject<UserTokenState>(JSON.parse(localStorage.getItem('UserTokenState')));
    this.userTokenState = this.userTokenStateSubject.asObservable();
    if (this.userTokenStateSubject) {
      this.decodeJwtToken();
    }
  }

  login(user: User) {
    return this.httpClient.post(this.url + "/login", user).pipe(map((res: UserTokenState) => {
      this.access_token = res.accessToken;
      this.refreshToken = res.refreshToken;
      localStorage.setItem('UserTokenState', JSON.stringify(res));
      this.userTokenStateSubject.next(res);
      this.decodeJwtToken();
    }));
  }

  register(client: RegistrationClient) {
    return this.httpClient.post(this.url, client);
  }

  changePassword(changePassword: ChangePassword) {
    return this.httpClient.put(this.url, changePassword);
  }

  logout() {
    //TODO: Delete everything from localstorage
    this.clearLocalStorage();
    this.router.navigate(['/login']);
  }

  clearLocalStorage() {
    this.access_token = null;
    this.refreshToken = null;
    localStorage.removeItem('UserTokenState');
  }

  getUserTokenState(): UserTokenState {
    if (this.userTokenStateSubject.value) {
      return this.userTokenStateSubject.value;
    }
    return null;
  }

  isLoggedIn() {
    return localStorage.getItem('UserTokenState') !== null;
  }

  decodeJwtToken() {
    if (!this.userTokenStateSubject.value) {
      return;
    }
    let jwtData = this.userTokenStateSubject.value.accessToken.split('.')[1];
    let decodedJwtJsonData = JSON.parse(window.atob(jwtData));
    this.role = decodedJwtJsonData.role;
    this.permissions = decodedJwtJsonData.permissions;
    this.loggedInUserEmail = decodedJwtJsonData.sub;
    return decodedJwtJsonData;
  }

  hasPermission(permissionName: string): boolean {
    if (!this.permissions) {
      return false;
    }
    let permission = this.permissions.find(permission => permission.name === permissionName);
    if (permission) {
      return true;
    }
    return false;
  }

  isAgent() {
    if (this.role) {
      return this.role === "ROLE_AGENT";
    }
    return true;
  }

  isClient() {
    if (this.role) {
      return this.role === "ROLE_CLIENT";
    }
    return true;
  }

  getLoggedInUserEmail() {
    return this.loggedInUserEmail;
  }

  refreshAccessToken() {
    return this.httpClient.post(this.url + "/refresh", null);
  }

  getAccessToken() {
    return this.access_token;
  }

  getRefreshToken() {
    return this.refreshToken;
  }

  setNewAccessToken(res: UserTokenState) {
    this.access_token = res.accessToken;
    this.refreshToken = res.refreshToken;
    localStorage.setItem('UserTokenState', JSON.stringify(res));
    this.userTokenStateSubject.next(res);
    this.decodeJwtToken();
  }
}
