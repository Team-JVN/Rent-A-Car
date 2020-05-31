import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Admin } from './../../../../model/admin';
import { AuthentificationService } from './../../../../service/authentification.service';
import { AdminService } from './../../../../service/admin.service';
import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";

@Component({
  selector: "app-edit-admin",
  templateUrl: "./edit-admin.component.html",
  styleUrls: ["./edit-admin.component.css"],
})
export class EditAdminComponent implements OnInit {
  editForm: FormGroup;
  loggedInAdmin: Admin = new Admin("", "");
  constructor(private formBuilder: FormBuilder, private adminService: AdminService, private toastr: ToastrService) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    });

    this.adminService.getLoggedInUser().subscribe(
      (responseData: Admin) => {
        this.loggedInAdmin = responseData;

        this.editForm.patchValue(
          {
            'name': this.loggedInAdmin.name
          }
        );
      },
      () => {
        this.toastr.error('Something goes wrong. Please try again.', 'Show clients');
      }
    );
  }

  editAdmin() {
    this.adminService.edit(new Admin(this.editForm.value.name, this.loggedInAdmin.email)).subscribe(
      () => {
        this.toastr.success("Success.", "Edit personal info");
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Edit personal info");
      }
    );
  }
}
