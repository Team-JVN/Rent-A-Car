import { Client } from './../../model/client';
import { ClientService } from './../../service/client.service';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-reject-request-to-register',
  templateUrl: './reject-request-to-register.component.html',
  styleUrls: ['./reject-request-to-register.component.css']
})
export class RejectRequestToRegisterComponent implements OnInit {

  rejectRequestToRegisterForm: FormGroup;

  constructor(
    public toastr: ToastrService, private clientService: ClientService, public dialogRef: MatDialogRef<RejectRequestToRegisterComponent>,
    @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
    this.rejectRequestToRegisterForm = new FormGroup({
      reason: new FormControl(null, Validators.required)
    });
  }

  reject(): void {
    if (this.rejectRequestToRegisterForm.invalid) {
      this.toastr.error('Please enter an explanation for rejection. ', 'Reject request to register ');
      return;
    }

    const id = this.data.client.id;
    const reason = this.rejectRequestToRegisterForm.value.reason;

    this.clientService.reject(id, reason).subscribe(
      () => {
        this.rejectRequestToRegisterForm.reset();
        this.dialogRef.close();
        this.toastr.success(
          'Request to register is rejected. Client will be notified ',
          'Reject request to register '
        );
        this.clientService.rejectSuccessEmitter.next(this.data.client);
      },
      () => {
        this.toastr.error("Request to register has already been approved/rejected.", 'Reject request to register');
        this.dialogRef.close();
      }
    )
  }

}
