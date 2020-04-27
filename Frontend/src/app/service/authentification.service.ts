import { map } from 'rxjs/operators';
import { User } from './../model/user';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { LoggedInUser } from '../model/loggedInUser';

@Injectable({
  providedIn: 'root'
})
export class AuthentificationService {

  url = environment.baseUrl + environment.auth;
  access_token = null;
  loggedInUserSubject: BehaviorSubject<LoggedInUser>;

  constructor(private httpClient: HttpClient) { }


  login(user: User) {
    return this.httpClient.post(this.url + "/login", user).pipe(map((res: LoggedInUser) => {
      this.access_token = res.userTokenState.accessToken;
      localStorage.setItem('LoggedInUser', JSON.stringify(res));
      this.loggedInUserSubject.next(res);
    }));
  }
}
