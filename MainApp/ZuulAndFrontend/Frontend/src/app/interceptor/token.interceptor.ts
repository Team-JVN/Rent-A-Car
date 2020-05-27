import { UserTokenState } from '../model/userTokenState';
import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable()
export class TokenInterceptor implements HttpInterceptor {
    userTokenState: UserTokenState;
    constructor() { }
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        this.userTokenState = JSON.parse(localStorage.getItem("UserTokenState"));

        if (this.userTokenState) {
            if (request.url.includes("refresh")) {
                if (this.userTokenState.refreshToken) {
                    request = request.clone({
                        setHeaders: {
                            Auth: `Bearer ${this.userTokenState.refreshToken}`
                        }
                    });
                }
            } else {
                if (this.userTokenState.accessToken) {
                    request = request.clone({
                        setHeaders: {
                            Auth: `Bearer ${this.userTokenState.accessToken}`
                        }
                    });
                }
            }

        }
        return next.handle(request);
    }
}
