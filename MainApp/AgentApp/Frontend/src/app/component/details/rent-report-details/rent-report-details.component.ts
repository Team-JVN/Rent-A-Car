import { MatDialog, MAT_DIALOG_DATA } from "@angular/material/dialog";

import { Component, OnInit, Inject } from "@angular/core";
import { ToastrService } from "ngx-toastr";
import { HttpErrorResponse } from "@angular/common/http";
import { Location } from "@angular/common";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";

import { RentReport } from "./../../../model/rentReport";

import { RentReportService } from "src/app/service/rent-report.service";

@Component({
  selector: "app-rent-report-details",
  templateUrl: "./rent-report-details.component.html",
  styleUrls: ["./rent-report-details.component.css"],
})
export class RentReportDetailsComponent implements OnInit {
  rentReport: RentReport;

  constructor(
    private toastr: ToastrService,
    private rentReportService: RentReportService,

    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.fetchRentReport();
  }
  fetchRentReport() {
    this.rentReportService.get(this.data.rentInfo.id).subscribe(
      (data: RentReport) => {
        this.rentReport = data;
        this.toastr.success("Success!", "Fetch rent report");
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Fetch rent report");
      }
    );
  }
}
