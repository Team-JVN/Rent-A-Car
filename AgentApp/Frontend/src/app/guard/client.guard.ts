import { AuthentificationService } from './../service/authentification.service';

import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { LoggedInUser } from '../model/loggedInUser';
@Injectable({ providedIn: 'root' })
export class ClientGuard implements CanActivate {
    loggedInUser: LoggedInUser;

    constructor(
        private router: Router,
        private authentificationService: AuthentificationService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

        this.loggedInUser = this.authentificationService.getLoggedInUser();

        if (this.loggedInUser) {
            if (this.loggedInUser.role === 'CLIENT') {
                return true;
            }
            else {
                this.router.navigate(['/error/non-authorized']);
                return false;
            }
        }

        this.router.navigate(['/error/non-authenticated']);
        return false;
    }
}
