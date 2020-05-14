import { Permission } from './../../../model/permission';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { RoleService } from './../../../service/role.service';
import { Component, OnInit } from '@angular/core';
import { Role } from 'src/app/model/role';

@Component({
  selector: 'app-edit-role',
  templateUrl: './edit-role.component.html',
  styleUrls: ['./edit-role.component.css']
})
export class EditRoleComponent implements OnInit {

  displayedColumns: string[] = ['permission'];
  roles: Role[];

  constructor(
    private roleService: RoleService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchRoles();
  }

  fetchRoles() {
    this.roleService.getRoles().subscribe(
      (data: Role[]) => {
        this.roles = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.roles = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Roles');
      }
    );
  }

}
