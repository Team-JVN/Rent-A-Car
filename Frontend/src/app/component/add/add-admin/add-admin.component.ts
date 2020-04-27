import { Admin } from './../../../model/admin';
import { AdminService } from './../../../service/admin.service';
import { Component, OnInit } from '@angular/core';
import { Agent } from './../../../model/agent';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-admin',
  templateUrl: './add-admin.component.html',
  styleUrls: ['./add-admin.component.css']
})
export class AddAdminComponent implements OnInit {
  addForm: FormGroup;

  constructor(private toastr: ToastrService, private adminService: AdminService,
    private formBuilder: FormBuilder, private dialogRef: MatDialogRef<AddAdminComponent>, ) { }

  ngOnInit() {
    this.addForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
    })
  }

  register() {
    if (this.addForm.invalid) {
      this.toastr.error("Please enter valid data", 'Registrate Admin');
      return;
    }
    const name = this.addForm.value.firstName + '|' + this.addForm.value.lastName;
    const agent = new Admin(name, this.addForm.value.email)

    this.adminService.add(agent).subscribe(
      (data: Agent) => {
        this.addForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Registrate Admin');
        this.adminService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Registrate Admin');
      }
    );
  }

}
