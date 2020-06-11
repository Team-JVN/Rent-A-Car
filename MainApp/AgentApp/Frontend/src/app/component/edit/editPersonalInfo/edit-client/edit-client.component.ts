import { FormControl, Validators, FormBuilder, FormGroup } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { Client } from 'src/app/model/client';
import { HttpErrorResponse } from '@angular/common/http';
import { ClientService } from 'src/app/service/client.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-client',
  templateUrl: './edit-client.component.html',
  styleUrls: ['./edit-client.component.css']
})
export class EditClientComponent implements OnInit {

  editForm: FormGroup;
  loggedInClient: Client = new Client("", "", "", "");
  constructor(
    private formBuilder: FormBuilder,
    private clientService: ClientService, private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required),
      address: new FormControl(null, Validators.required),
      phoneNumber: new FormControl(null, [
        Validators.required,
        Validators.minLength(9),
        Validators.maxLength(10),
        Validators.pattern("0[0-9]+"),
      ]),
    });

    this.clientService.getLoggedInUser().subscribe(
      (responseData: Client) => {
        this.loggedInClient = responseData;

        this.editForm.patchValue(
          {
            'name': this.loggedInClient.name,
            'address': this.loggedInClient.address,
            'phoneNumber': this.loggedInClient.phoneNumber,
          }
        );
      },
      () => {
        this.toastr.error('Something goes wrong. Please try again.', 'Show clients');
      }
    );
  }

  editClient() {
    this.clientService.edit(new Client(this.editForm.value.name, this.loggedInClient.email, this.editForm.value.address,
      this.editForm.value.phoneNumber)).subscribe(
        () => {
          this.toastr.success("Success.", "Edit personal info");
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Edit personal info");
        }
      );
  }

}
