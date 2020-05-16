import { AuthentificationService } from './../service/authentification.service';

import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
@Injectable({ providedIn: 'root' })
export class ClientGuard implements CanActivate {
    constructor(
        private router: Router,
        private authentificationService: AuthentificationService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (this.authentificationService.isLoggedIn()) {
            if (this.authentificationService.isClient()) {
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
