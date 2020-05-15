import { RentInfo } from "./../../../model/rentInfo";
import { RentReportService } from "src/app/service/rent-report.service";
import { RentReport } from "./../../../model/rentReport";
import { Component, OnInit } from "@angular/core";
import { MatTableDataSource } from "@angular/material/table";
import { Subscription } from "rxjs";
import { Router } from "@angular/router";
import { MatDialog } from "@angular/material/dialog";
import { ToastrService } from "ngx-toastr";
import { HttpErrorResponse } from "@angular/common/http";
import {
  trigger,
  state,
  transition,
  style,
  animate,
} from "@angular/animations";
import { AddRentReportComponent } from "../../add/add-rent-report/add-rent-report.component";

@Component({
  selector: "app-list-rent-reports",
  templateUrl: "./list-rent-reports.component.html",
  styleUrls: ["./list-rent-reports.component.css"],
  animations: [
    trigger("detailExpand", [
      state("collapsed", style({ height: "0px", minHeight: "0" })),
      state("expanded", style({ height: "*" })),
      transition(
        "expanded <=> collapsed",
        animate("225ms cubic-bezier(0.4, 0.0, 0.2, 1)")
      ),
    ]),
  ],
})
export class ListRentReportsComponent implements OnInit {
  displayedColumns: string[] = ["car", "madeMileage"];
  expandedElement: RentReport | null;
  rentReportsDataSource: MatTableDataSource<RentReport>;
  createSuccess: Subscription;

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private rentReportService: RentReportService,
    private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.fetchRentReports();
    this.createSuccess = this.rentReportService.createSuccessEmitter.subscribe(
      () => {
        this.fetchRentReports();
      }
    );
  }

  fetchRentReports() {
    this.rentReportService.getAll().subscribe(
      (data: RentReport[]) => {
        this.rentReportsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: RentReport[] = [];
        this.rentReportsDataSource = new MatTableDataSource(data);
        this.toastr.error(httpErrorResponse.error.message, "Show Rent Reports");
      }
    );
  }

  // edit(element: AdvertisementWithPicturesDTO) {
  //   this.dialog.open(EditAdvertisementComponent, { data: element });

  // }

  //   delete(element: RentRequest) {
  //     this.rentRequestService.delete(element.id).subscribe(
  //       () => {
  //         this.fetchRentRequests(this.status);
  //         this.toastr.success(
  //           "Successfully deleted Rent Request!",
  //           "Delete Rent Request"
  //         );
  //       },
  //       (httpErrorResponse: HttpErrorResponse) => {
  //         this.toastr.error(
  //           httpErrorResponse.error.message,
  //           "Delete Rent Request"
  //         );
  //       }
  //     );
  //   }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(["/advertisement/" + rentInfo.advertisement.id]);
  }
}
