import { AdvertisementFromSearch } from "./../../../model/advertisementFromSearch";
import { AuthentificationService } from "./../../../service/authentification.service";
import { RentInfo } from "./../../../model/rentInfo";
import { RentRequest } from "./../../../model/rentRequest";
import { AddClientComponent } from "./../add-client/add-client.component";
import { HttpErrorResponse } from "@angular/common/http";
import { Client } from "./../../../model/client";
import { Subscription } from "rxjs";
import {
  MatDialogRef,
  MatDialog,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { ClientService } from "./../../../service/client.service";
import { ToastrService } from "ngx-toastr";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
  ValidatorFn,
} from "@angular/forms";
import { Component, OnInit, Inject } from "@angular/core";
import { RentRequestService } from "src/app/service/rent-request.service";
import { formatDate } from "@angular/common";

const DateValidator: ValidatorFn = (fg: FormGroup) => {
  const from = fg.get("dateFrom").value;
  const to = fg.get("dateTo").value;
  if (!from || !to) {
    return null;
  }
  return from !== null && to !== null && from < to
    ? null
    : { validError: true };
};

@Component({
  selector: "app-add-rent-request",
  templateUrl: "./add-rent-request.component.html",
  styleUrls: ["./add-rent-request.component.css"],
})
export class AddRentRequestComponent implements OnInit {
  clientForm: FormGroup;
  informationForm: FormGroup;
  minDate = new Date();
  successCreatedClient: Subscription;
  clients: Client[] = [];

  constructor(
    private toastr: ToastrService,
    private clientService: ClientService,
    private rentRequestService: RentRequestService,
    private authService: AuthentificationService,
    private dialogRef: MatDialogRef<AddRentRequestComponent>,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public selectedItem: AdvertisementFromSearch
  ) {}

  ngOnInit() {
    if (this.minDate < new Date(this.selectedItem.dateFrom)) {
      this.minDate = new Date(this.selectedItem.dateFrom);
    }
    this.clientForm = this.formBuilder.group({
      client: new FormControl(null, Validators.required),
    });

    this.informationForm = this.formBuilder.group(
      {
        dateFrom: new FormControl(null, Validators.required),
        timeFrom: new FormControl(null, [Validators.required]),
        dateTo: new FormControl(null, [Validators.required]),
        timeTo: new FormControl(null, [Validators.required]),
        optedForCDW: new FormControl(null),
      },
      {
        validator: [DateValidator],
      }
    );

    this.successCreatedClient = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchClients();
      }
    );

    this.fetchClients();
  }

  fetchClients() {
    this.clientService.getClientsForRentRequest().subscribe(
      (data: Client[]) => {
        this.clients = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Client[] = [];
        this.clients = data;
        this.toastr.error(httpErrorResponse.error.message, "Show Clients");
      }
    );
  }

  getSelectedClient() {
    return this.clientForm.get("client").value;
  }

  create() {
    if (this.informationForm.invalid) {
      this.toastr.error(
        "Please enter valid date and time",
        "Create Rent Request"
      );
      return;
    }

    const dateFrom = formatDate(
      this.informationForm.value.dateFrom,
      "yyyy-MM-dd",
      "en-US"
    );
    const dateTimeFrom = dateFrom + " " + this.informationForm.value.timeFrom;
    const dateTo = formatDate(
      this.informationForm.value.dateTo,
      "yyyy-MM-dd",
      "en-US"
    );
    const dateTimeTo = dateTo + " " + this.informationForm.value.timeTo;

    let optedForCDW = null;
    if (this.selectedItem.cdw) {
      optedForCDW = this.informationForm.value.optedForCDW;
    }

    let comments = [];
    const newRentInfo = new RentInfo(
      dateTimeFrom,
      dateTimeTo,
      optedForCDW,
      this.selectedItem,
      comments
    );
    let rentInfos = [];

    rentInfos.push(newRentInfo);
    let messages = [];

    const rentRequest = new RentRequest(
      this.clientForm.value.client,
      rentInfos,
      messages
    );

    this.rentRequestService.create(rentRequest).subscribe(
      (data: RentRequest) => {
        this.clientForm.reset();
        this.informationForm.reset();
        this.dialogRef.close();
        this.toastr.success("Success.", "Create Rent Request");
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(
          httpErrorResponse.error.message,
          "Create Rent Request"
        );
      }
    );
  }

  openAddClient() {
    this.dialog.open(AddClientComponent);
  }
}
