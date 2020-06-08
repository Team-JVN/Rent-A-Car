import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthentificationService } from '../service/authentification.service';
@Injectable({ providedIn: 'root' })
export class GetStatisticsGuard implements CanActivate {
    constructor(
        private router: Router,
        private authentificationService: AuthentificationService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

        if (this.authentificationService.isLoggedIn()) {
            if (this.authentificationService.hasPermission("GET_STATISTICS")) {
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