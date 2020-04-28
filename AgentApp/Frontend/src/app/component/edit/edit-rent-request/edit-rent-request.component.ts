import { RentRequest } from './../../../model/rentRequest';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, ValidatorFn, FormControl, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Client } from 'src/app/model/client';
import { ToastrService } from 'ngx-toastr';
import { ClientService } from 'src/app/service/client.service';
import { RentRequestService } from 'src/app/service/rent-request.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AddClientComponent } from '../../add/add-client/add-client.component';
import { formatDate } from '@angular/common';
import { RentInfo } from 'src/app/model/rentInfo';

const DateValidator: ValidatorFn = (fg: FormGroup) => {
  const from = fg.get('dateFrom').value;
  const to = fg.get('dateTo').value;
  if (!from || !to) {
    return null;
  }
  return from !== null && to !== null && from < to ? null : { validError: true };
};

@Component({
  selector: 'app-edit-rent-request',
  templateUrl: './edit-rent-request.component.html',
  styleUrls: ['./edit-rent-request.component.css']
})
export class EditRentRequestComponent implements OnInit {

  clientForm: FormGroup;
  informationForm: FormGroup;
  minDate = new Date();
  successCreatedClient: Subscription;
  clients: Client[] = [];

  constructor(private toastr: ToastrService, private clientService: ClientService, private rentRequestService: RentRequestService,
    private dialogRef: MatDialogRef<EditRentRequestComponent>, private formBuilder: FormBuilder, public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public selectedItem: RentRequest) { }

  ngOnInit() {
    this.clientForm = this.formBuilder.group({
      client: new FormControl(this.selectedItem.client, Validators.required),
    })
    console.log(new Date(this.selectedItem.rentInfos[0].dateTimeFrom).getHours() + ':' + new Date(this.selectedItem.rentInfos[0].dateTimeFrom).getMinutes())
    this.informationForm = this.formBuilder.group({
      dateFrom: new FormControl(new Date(this.selectedItem.rentInfos[0].dateTimeFrom.substring(0, 10)), Validators.required),
      timeFrom: new FormControl(this.selectedItem.rentInfos[0].dateTimeFrom.slice(-5), [Validators.required]),
      dateTo: new FormControl(new Date(this.selectedItem.rentInfos[0].dateTimeTo.substring(0, 10)), [Validators.required]),
      timeTo: new FormControl(this.selectedItem.rentInfos[0].dateTimeTo.slice(-5), [Validators.required]),
      pickUpPoint: new FormControl(this.selectedItem.rentInfos[0].pickUpPoint, Validators.required),
      optedForCDW: new FormControl(this.selectedItem.rentInfos[0].optedForCDW)
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
        this.selectClients();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Client[] = []
        this.clients = data;
        this.toastr.error(httpErrorResponse.error.message, 'Show Clients');
      }
    );
  }

  selectClients() {
    this.clients.forEach((element: Client) => {
      if (element.id === this.selectedItem.client.id) {
        this.clientForm.controls['client'].setValue(element);
      }
    });
  }

  getSelectedClient() {
    return this.clientForm.get('client').value;
  }

  edit() {
    if (this.informationForm.invalid) {
      this.toastr.error("Please enter valid date and time", 'Edit Rent Request');
      return;
    }

    const dateFrom = formatDate(this.informationForm.value.dateFrom, 'yyyy-MM-dd', 'en-US')
    const dateTimeFrom = dateFrom + ' ' + this.informationForm.value.timeFrom;
    const dateTo = formatDate(this.informationForm.value.dateTo, 'yyyy-MM-dd', 'en-US')
    const dateTimeTo = dateTo + ' ' + this.informationForm.value.timeTo;

    var cdw = this.informationForm.value.optedForCDW;
    if (!this.selectedItem.rentInfos[0].advertisement.cdw) {
      cdw = null;
    }

    const rentInfo = new RentInfo(dateTimeFrom, dateTimeTo, this.informationForm.value.pickUpPoint, cdw, this.selectedItem.rentInfos[0].advertisement, null, this.selectedItem.rentInfos[0].id);
    var rentInfos = [];
    rentInfos.push(rentInfo);
    const rentRequest = new RentRequest(this.clientForm.value.client, rentInfos, this.selectedItem.totalPrice, this.selectedItem.rentRequestStatus, this.selectedItem.id);

    this.rentRequestService.edit(rentRequest).subscribe(
      (data: RentRequest) => {
        this.clientForm.reset();
        this.informationForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success.', 'Edit Rent Request');
        this.rentRequestService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Rent Request');
      }
    );
  }

  openAddClient() {
    this.dialog.open(AddClientComponent);
  }

}
