import { RentInfo } from './../../../model/rentInfo';
import { RentRequest } from './../../../model/rentRequest';
import { AddClientComponent } from './../add-client/add-client.component';
import { HttpErrorResponse } from '@angular/common/http';
import { Client } from './../../../model/client';
import { Subscription } from 'rxjs';
import { Advertisement } from './../../../model/advertisement';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClientService } from './../../../service/client.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl, Validators, ValidatorFn } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { RentRequestService } from 'src/app/service/rent-request.service';
import { formatDate } from '@angular/common';

const DateValidator: ValidatorFn = (fg: FormGroup) => {
  const from = fg.get('dateFrom').value;
  const to = fg.get('dateTo').value;
  if (!from || !to) {
    return null;
  }
  return from !== null && to !== null && from < to ? null : { validError: true };
};

@Component({
  selector: 'app-add-rent-request',
  templateUrl: './add-rent-request.component.html',
  styleUrls: ['./add-rent-request.component.css']
})
export class AddRentRequestComponent implements OnInit {

  clientForm: FormGroup;
  informationForm: FormGroup;
  minDate = new Date();
  successCreatedClient: Subscription;
  clients: Client[] = [];

  constructor(private toastr: ToastrService, private clientService: ClientService, private rentRequestService: RentRequestService,
    private dialogRef: MatDialogRef<AddRentRequestComponent>, private formBuilder: FormBuilder, public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public selectedItem: Advertisement) { }

  ngOnInit() {
    this.clientForm = this.formBuilder.group({
      client: new FormControl(null, Validators.required),
    })

    this.informationForm = this.formBuilder.group({
      dateFrom: new FormControl(null, Validators.required),
      timeFrom: new FormControl(null, [Validators.required]),
      dateTo: new FormControl(null, [Validators.required]),
      timeTo: new FormControl(null, [Validators.required]),
      pickUpPoint: new FormControl(null, Validators.required),
      optedForCDW: new FormControl(null)
    }, {
      validator: [DateValidator]
    })

    this.successCreatedClient = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchClients()
      }
    );

    this.fetchClients();
  }

  fetchClients() {
    this.clientService.getClients().subscribe(
      (data: Client[]) => {
        this.clients = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Client[] = []
        this.clients = data;
        this.toastr.error(httpErrorResponse.error.message, 'Show Clients');
      }
    );
  }

  getSelectedClient() {
    return this.clientForm.get('client').value;
  }

  create() {
    if (this.informationForm.invalid) {
      this.toastr.error("Please enter valid date and time", 'Create Rent Request');
      return;
    }
    const dateFrom = formatDate(this.informationForm.value.dateFrom, 'dd-MM-yyyy', 'en-US')
    const dateTimeFrom = this.informationForm.value.timeFrom + ' ' + dateFrom;
    const dateTo = formatDate(this.informationForm.value.dateTo, 'dd-MM-yyyy', 'en-US')
    const dateTimeTo = this.informationForm.value.timeTo + ' ' + dateTo;

    var cdw = this.informationForm.value.optedForCDW;
    if (!this.selectedItem.cdw) {
      cdw = null;
    }
    const rentInfo = new RentInfo(dateTimeFrom, dateTimeTo, this.informationForm.value.pickUpPoint, cdw, this.selectedItem);
    var rentInfos = [];
    rentInfos.push(rentInfo);
    const rentRequest = new RentRequest(this.clientForm.value.client, rentInfos);

    this.rentRequestService.create(rentRequest).subscribe(
      (data: RentRequest) => {
        this.clientForm.reset();
        this.informationForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success.', 'Create Rent Request');
        this.rentRequestService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Rent Request');
      }
    );
  }

  openAddClient() {
    this.dialog.open(AddClientComponent);
  }

}
