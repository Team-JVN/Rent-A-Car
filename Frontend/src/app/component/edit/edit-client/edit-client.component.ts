import { Client } from './../../../model/client';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClientService } from './../../../service/client.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-edit-client',
  templateUrl: './edit-client.component.html',
  styleUrls: ['./edit-client.component.css']
})
export class EditClientComponent implements OnInit {

  editForm: FormGroup;

  constructor(private toastr: ToastrService, private clientService: ClientService,
    private dialogRef: MatDialogRef<EditClientComponent>, private formBuilder: FormBuilder, @Inject(MAT_DIALOG_DATA) public selectedItem: Client) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      name: new FormControl(this.selectedItem.name, Validators.required),
      address: new FormControl(this.selectedItem.address, Validators.required),
      phoneNumber: new FormControl(this.selectedItem.phoneNumber, [Validators.required, Validators.minLength(9), Validators.maxLength(10), Validators.pattern("0[0-9]+")])
    })
  }

  edit() {
    const client = new Client(this.editForm.value.name, this.selectedItem.email, this.editForm.value.address, this.editForm.value.phoneNumber, this.selectedItem.id);
    this.clientService.edit(client).subscribe(
      (data: Client) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Edit Client');
        this.clientService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Client');
      }
    );
  }
}
