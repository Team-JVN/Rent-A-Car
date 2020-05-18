import { Client } from './../../../model/client';
import { MatDialogRef } from '@angular/material/dialog';
import { ClientService } from './../../../service/client.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-client',
  templateUrl: './add-client.component.html',
  styleUrls: ['./add-client.component.css']
})
export class AddClientComponent implements OnInit {

  createForm: FormGroup;

  constructor(private toastr: ToastrService, private clientService: ClientService,
    private dialogRef: MatDialogRef<AddClientComponent>, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required),
      email: new FormControl(null, [Validators.required, Validators.email]),
      address: new FormControl(null, Validators.required),
      phoneNumber: new FormControl(null, [Validators.required, Validators.minLength(9), Validators.maxLength(10), Validators.pattern("0[0-9]+")])
    })
  }

  create() {
    const client = new Client(this.createForm.value.name, this.createForm.value.email, this.createForm.value.address, this.createForm.value.phoneNumber);
    this.clientService.create(client).subscribe(
      (data: Client) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Create Client');
        this.clientService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Client');
      }
    );
  }
}
