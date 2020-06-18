import { RentInfo } from "./../../../model/rentInfo";
import { RentRequest } from "./../../../model/rentRequest";
import { AddClientComponent } from "./../add-client/add-client.component";
import { HttpErrorResponse } from "@angular/common/http";
import { Client } from "./../../../model/client";
import { Subscription } from "rxjs";
import { Advertisement } from "./../../../model/advertisement";
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
import { RentReport } from "src/app/model/rentReport";
import { RentReportService } from "src/app/service/rent-report.service";

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
  selector: "app-add-rent-report",
  templateUrl: "./add-rent-report.component.html",
  styleUrls: ["./add-rent-report.component.css"],
})
export class AddRentReportComponent implements OnInit {
  createForm: FormGroup;

  minDate = new Date();
  successCreatedClient: Subscription;
  clients: Client[] = [];

  constructor(
    private toastr: ToastrService,
    private rentReportService: RentReportService,
    private dialogRef: MatDialogRef<AddRentReportComponent>,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public selectedItem: RentInfo
  ) {}

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      mileage: new FormControl(null, [
        Validators.required,
        Validators.minLength(1),
        Validators.pattern("[1-9][0-9]*"),
      ]),
      comment: new FormControl(null, [Validators.required]),
    });
  }

  getSelectedRentInfo() {
    return this.selectedItem.advertisement;
  }

  create() {
    const rentReport = new RentReport(
      this.selectedItem,
      this.createForm.value.mileage,
      this.createForm.value.comment
    );
    this.rentReportService.create(rentReport).subscribe(
      (data: RentReport) => {
        this.createForm.reset();
        this.dialogRef.close(true);
        this.toastr.success("Success!", "Create Rent Report");
        this.rentReportService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(
          httpErrorResponse.error.message,
          "Create Rent Report"
        );
      }
    );
  }
}
