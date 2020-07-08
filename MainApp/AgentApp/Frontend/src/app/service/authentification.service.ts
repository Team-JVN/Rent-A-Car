import { UserTokenState } from './../model/userTokenState';
import { ChangePassword } from './../model/changePassword';
import { map } from 'rxjs/operators';
import { User } from './../model/user';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
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
    let header = new HttpHeaders();
    header.append('Cache-Control', 'no-store, no-cache, must-revalidate, post- check=0, pre-check=0');
    header.append('Pragma', 'no-cache');
    header.append('Expires', '0');
    return this.httpClient.post(this.url + "/login", user, { headers: header }).pipe(map((res: UserTokenState) => {
      this.access_token = res.accessToken;
      this.refreshToken = res.refreshToken;
      localStorage.setItem('UserTokenState', JSON.stringify(res));
      this.userTokenStateSubject.next(res);
      this.decodeJwtToken();
    }));
  }

  register(client: RegistrationClient) {
    let header = new HttpHeaders();
    header.append('Cache-Control', 'no-store, no-cache, must-revalidate, post- check=0, pre-check=0');
    header.append('Pragma', 'no-cache');
    header.append('Expires', '0');
    return this.httpClient.post(this.url + "/register", client, { headers: header });
  }

  changePassword(changePassword: ChangePassword) {
    let header = new HttpHeaders();
    header.append('Cache-Control', 'no-store, no-cache, must-revalidate, post- check=0, pre-check=0');
    header.append('Pragma', 'no-cache');
    header.append('Expires', '0');
    return this.httpClient.put(this.url, changePassword, { headers: header });
  }

  requestToken(email: string) {
    return this.httpClient.post(this.url, { email: email });
  }

  resetPassword(token: string, newPassword: string) {
    let params = new HttpParams();
    params = params.append('t', token);
    let header = new HttpHeaders();
    header.append('Cache-Control', 'no-store, no-cache, must-revalidate, post- check=0, pre-check=0');
    header.append('Pragma', 'no-cache');
    header.append('Expires', '0');
    return this.httpClient.put(this.url + '/reset-password', { newPassword: newPassword }, { params: params, headers: header });
  }

  logout() {
    //TODO: Delete everything from localstorage
    this.clearLocalStorage();
    this.router.navigate(['/login']);
  }

  clearLocalStorage() {
    this.access_token = null;
    this.refreshToken = null;
    localStorage.removeItem('rentInfos');
    localStorage.removeItem('UserTokenState');
    localStorage.removeItem('searchParams');
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
