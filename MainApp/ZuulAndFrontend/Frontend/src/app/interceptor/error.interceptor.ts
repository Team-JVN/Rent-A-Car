import { AuthentificationService } from 'src/app/service/authentification.service';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError, BehaviorSubject, Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { UserTokenState } from '../model/userTokenState';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    isRefreshingToken: boolean = false;
    tokenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(null);
    constructor(private router: Router, private dialogRef: MatDialog, private authentificationService: AuthentificationService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<any> {
        return next.handle(request).pipe(catchError((err) => {
            if (err.status === 401) {
                if (request.url.includes("refresh") || request.url.includes("login")) {
                    this.dialogRef.closeAll();
                    this.authentificationService.clearLocalStorage();
                    this.router.navigate(['/error/non-authenticated']);
                    return throwError(err);
                }

                if (!this.isRefreshingToken) {
                    this.isRefreshingToken = true;
                    this.tokenSubject.next(null);

                    this.authentificationService.refreshAccessToken().toPromise().then(
                        (res: UserTokenState) => {
                            this.authentificationService.access_token = null;
                            localStorage.removeItem('UserTokenState');
                            this.authentificationService.setNewAccessToken(res);
                            this.tokenSubject.next(res.accessToken);
                            this.isRefreshingToken = false;
                            return of(true);
                        },
                        () => {
                            this.authentificationService.clearLocalStorage();
                            this.isRefreshingToken = false;
                            this.router.navigate(['/error/non-authenticated']);
                        }
                    );
                    return of(true);
                } else {
                    this.dialogRef.closeAll();
                    this.authentificationService.clearLocalStorage();
                    this.router.navigate(['/error/non-authenticated']);
                }
            } else if (err.status === 403) {
                this.dialogRef.closeAll();
                this.router.navigate(['/error/non-authorized']);
            }
            return throwError(err);
        }))
    }

}