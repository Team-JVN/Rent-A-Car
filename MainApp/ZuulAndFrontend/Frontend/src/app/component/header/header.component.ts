import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs/operators';
import { AuthentificationService } from 'src/app/service/authentification.service';

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

  isLoggedIn() {
    return this.authentificationService.isLoggedIn();
  }

  onLogout() {
    this.authentificationService.logout();
    this.router.navigate(['/login']);
  }

  hasPermissionManageCodeBooks() {
    return this.authentificationService.hasPermission("MANAGE_CODE_BOOKS");
  }

  hasPermissionManageCars() {
    return this.authentificationService.hasPermission("MANAGE_CARS");
  }

  hasPermissionManageUsers() {
    return this.authentificationService.hasPermission("MANAGE_USERS");
  }

  hasPermissionManageRoles() {
    return this.authentificationService.hasPermission("MANAGE_ROLES");
  }

  hasPermissionAdminEditProfile() {
    return this.authentificationService.hasPermission("ADMIN_EDIT_PROFILE");
  }

  hasPermissionAgentEditProfile() {
    return this.authentificationService.hasPermission("AGENT_EDIT_PROFILE");
  }

  hasPermissionClientProfile() {
    return this.authentificationService.hasPermission("CLIENT_EDIT_PROFILE");
  }

  hasPermissionManagePricelists() {
    return this.authentificationService.hasPermission("MANAGE_PRICE_LISTS");
  }

  hasPermissionManageAdvertisements() {
    return this.authentificationService.hasPermission("MANAGE_ADVERTISEMENTS");
  }
}
