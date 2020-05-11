
import { ChangePassword } from './../model/changePassword';
import { map } from 'rxjs/operators';
import { User } from './../model/user';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { LoggedInUser } from '../model/loggedInUser';
import { Router } from '@angular/router';
import { RegistrationClient } from '../model/registrationClient';

@Injectable({
  providedIn: 'root'
})
export class AuthentificationService {

  url = environment.baseUrl + environment.auth;
  access_token = null;
  loggedInUserSubject: BehaviorSubject<LoggedInUser>;
  loggedInUser: Observable<LoggedInUser>;

  constructor(private httpClient: HttpClient, private router: Router) {
    this.loggedInUserSubject = new BehaviorSubject<LoggedInUser>(JSON.parse(localStorage.getItem('LoggedInUser')));
    this.loggedInUser = this.loggedInUserSubject.asObservable();
  }


  login(user: User) {
    return this.httpClient.post(this.url + "/login", user).pipe(map((res: LoggedInUser) => {
      this.access_token = res.userTokenState.accessToken;
      console.log(res)
      localStorage.setItem('LoggedInUser', JSON.stringify(res));
      this.loggedInUserSubject.next(res);
    }));
  }


  register(client: RegistrationClient) {
    return this.httpClient.post(this.url, client);
  }

  changePassword(changePassword: ChangePassword) {
    return this.httpClient.put(this.url, changePassword);
  }

  logout() {
    this.access_token = null;
    localStorage.removeItem('LoggedInUser');
    this.router.navigate(['/login']);
  }

  getLoggedInUser(): LoggedInUser {
    return this.loggedInUserSubject.value;
  }

  isLoggedIn() {
    return localStorage.getItem('LoggedInUser') !== null;
  }

  isAgent() {
    if (this.isLoggedIn()) {
      return this.loggedInUserSubject.value.role === "ROLE_AGENT";
    }
  }

  isClient() {
    if (this.isLoggedIn()) {
      return this.loggedInUserSubject.value.role === "ROLE_CLIENT";
    }
  }
}
