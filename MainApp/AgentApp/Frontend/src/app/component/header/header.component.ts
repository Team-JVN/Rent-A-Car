import { AuthentificationService } from 'src/app/service/authentification.service';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(
    private router: Router,
    private breakpointObserver: BreakpointObserver,
    private authentificationService: AuthentificationService
  ) { }

  isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map((result) => result.matches),
      shareReplay()
    );

  ngOnInit() {

  }

  hasPermissionManageAdvertisements() {
    return this.authentificationService.hasPermission("MANAGE_ADVERTISEMENTS");
  }

  hasPermissionManageCodeBooks() {
    return this.authentificationService.hasPermission("MANAGE_CODE_BOOKS");
  }

  hasPermissionManageCars() {
    return this.authentificationService.hasPermission("MANAGE_CARS");
  }

  hasPermissionManageClients() {
    return this.authentificationService.hasPermission("MANAGE_CLIENTS");
  }

  hasPermissionManagePriceLists() {
    return this.authentificationService.hasPermission("MANAGE_PRICE_LISTS");
  }

  hasPermissionManageRentReports() {
    return this.authentificationService.hasPermission("MANAGE_RENT_REPORTS");
  }

  hasPermissionManageRoles() {
    return this.authentificationService.hasPermission("MANAGE_ROLES");
  }

  hasPermissionAgentEditProfile() {
    return this.authentificationService.hasPermission("AGENT_EDIT_PROFILE");
  }

  hasPermissionClientEditProfile() {
    return this.authentificationService.hasPermission("CLIENT_EDIT_PROFILE");
  }

  hasPermissionMyRentRequests() {
    return this.authentificationService.hasPermission("MY_RENT_REQUESTS");
  }

  isLoggedIn() {
    return this.authentificationService.isLoggedIn();
  }

  onLogout() {
    this.authentificationService.logout();
    this.router.navigate(['/login']);
  }
}
