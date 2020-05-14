import { MatTableDataSource } from '@angular/material/table';
import { HttpErrorResponse } from '@angular/common/http';
import { Permission } from './../../../model/permission';
import { PermissionService } from './../../../service/permission.service';
import { Role } from './../../../model/role';
import { Component, OnInit, Input } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { RoleService } from 'src/app/service/role.service';

@Component({
  selector: 'app-view-permissions',
  templateUrl: './view-permissions.component.html',
  styleUrls: ['./view-permissions.component.css']
})
export class ViewPermissionsComponent implements OnInit {
  @Input() role: Role;
  permissionsDataSource: MatTableDataSource<Permission>;
  displayedColumns: string[] = ['checkbox', 'name'];
  allPermissions: Permission[];

  constructor(private permissionService: PermissionService, private toastr: ToastrService,
    private roleService: RoleService) { }

  ngOnInit() {
    //this.fetchPermissions();
    const permissions = [new Permission("MANAGE_CODE_BOOKS", 1), new Permission("MANAGE_CLIENTS", 2), new Permission("MANAGE_AGENTS", 3), new Permission("MANAGE_CLIENT5", 4)]
    this.permissionsDataSource = new MatTableDataSource(permissions);
    this.allPermissions = permissions;
    this.selectPermissions();
  }


  fetchPermissions() {
    this.permissionService.getPermissions().subscribe(
      (data: Permission[]) => {
        this.permissionsDataSource = new MatTableDataSource(data);
        this.allPermissions = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const permissions = [];
        this.allPermissions = permissions;
        this.permissionsDataSource = new MatTableDataSource(permissions);
        this.toastr.error(httpErrorResponse.error.message, 'Show Permissions');
      }
    );
  }

  selectPermissions() {
    this.allPermissions.forEach(permission => {
      this.role.permissions.forEach(rolePermission => {
        if (rolePermission.id == permission.id) {
          permission.isAssigned = true;
        }
      })
    });
  }

  changeValue(element: Permission) {
    element.isAssigned = !element.isAssigned;
  }

  getSelectedPermissions() {
    const selectedPermission: Permission[] = [];
    this.allPermissions.forEach(permission => {
      if (permission.isAssigned) {
        selectedPermission.push(permission);
      }
    });
    return selectedPermission;
  }

  saveChanges() {
    this.role.permissions = this.getSelectedPermissions();
    console.log(this.role)
    this.roleService.edit(this.role).subscribe(
      () => {
        this.toastr.success('Successfully changed permissions.', 'Edit Permissions');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Permissions');
      }
    );
  }
}
