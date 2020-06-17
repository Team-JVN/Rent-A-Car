import { Component, OnInit, Inject } from "@angular/core";
import {
  ValidatorFn,
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { Subscription } from "rxjs";
import { Client } from "src/app/model/client";
import { ToastrService } from "ngx-toastr";
import { RentReportService } from "./../../../service/rent-report.service";
import {
  MatDialogRef,
  MatDialog,
  MAT_DIALOG_DATA,
} from "@angular/material/dialog";
import { RentReport } from "src/app/model/rentReport";
import { HttpErrorResponse } from "@angular/common/http";
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
    @Inject(MAT_DIALOG_DATA) public data: any
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
    return this.data.rentInfo.advertisement;
  }

  create() {
    const rentReport = new RentReport(
      // this.data.rentInfo,
      this.createForm.value.mileage,
      this.createForm.value.comment
    );
    console.log(rentReport);
    this.rentReportService.create(rentReport, this.data.rentInfo.id).subscribe(
      (data: RentReport) => {
        this.createForm.reset();
        this.dialogRef.close();
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
