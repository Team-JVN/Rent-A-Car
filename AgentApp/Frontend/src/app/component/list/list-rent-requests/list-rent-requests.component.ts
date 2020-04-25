import { RentInfo } from "./../../../model/rentInfo";
import { RentRequestService } from "src/app/service/rent-request.service";
import { RentRequest } from "./../../../model/rentRequest";
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
import { RentReportService } from "src/app/service/rent-report.service";
import { RentReport } from "src/app/model/rentReport";

@Component({
  selector: "app-list-rent-requests",
  templateUrl: "./list-rent-requests.component.html",
  styleUrls: ["./list-rent-requests.component.css"],
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
export class ListRentRequestsComponent implements OnInit {
  displayedColumns: string[] = ["client", "totalPrice"];
  expandedElement: RentRequest | null;
  rentRequestsDataSource: MatTableDataSource<RentRequest>;
  createSuccess: Subscription;
  status: string = "all";

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private rentRequestService: RentRequestService,
    private toastr: ToastrService,
    private rentReportService: RentReportService
  ) {}

  ngOnInit() {
    this.fetchRentRequests("all");
    this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
      () => {
        this.fetchRentRequests(this.status);
      }
    );
  }

  fetchRentRequests(status: string) {
    this.rentRequestService.getRentRequests(status).subscribe(
      (data: RentRequest[]) => {
        // for (let rentRequest of data) {
        //   rentRequest.rentInfos.map((rentInfo) => {
        //     rentInfo.availableCreatingRentReport = this.fetchRentReports(
        //       rentInfo
        //     );
        //   });
        // }

        this.rentRequestsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: RentRequest[] = [];
        this.rentRequestsDataSource = new MatTableDataSource(data);
        this.toastr.error(
          httpErrorResponse.error.message,
          "Show Rent Requests"
        );
      }
    );
  }

  // edit(element: AdvertisementWithPicturesDTO) {
  //   this.dialog.open(EditAdvertisementComponent, { data: element });

  // }

  delete(element: RentRequest) {
    this.rentRequestService.delete(element.id).subscribe(
      () => {
        this.fetchRentRequests(this.status);
        this.toastr.success(
          "Successfully deleted Rent Request!",
          "Delete Rent Request"
        );
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(
          httpErrorResponse.error.message,
          "Delete Rent Request"
        );
      }
    );
  }

  createRentReport(rentInfo: RentInfo) {
    this.dialog.open(AddRentReportComponent, { data: rentInfo });
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(["/advertisement/" + rentInfo.advertisement.id]);
  }

  // fetchRentReports() {
  //   this.rentReportService.getAll().subscribe(
  //     (data: RentReport[]) => {
  //       this.rentReportsDataSource = new MatTableDataSource(data);
  //       console.log(data);
  //     },
  //     (httpErrorResponse: HttpErrorResponse) => {
  //       const data: RentReport[] = [];
  //       this.rentReportsDataSource = new MatTableDataSource(data);
  //       this.toastr.error(httpErrorResponse.error.message, "Show Rent Reports");
  //     }
  //   );
  // }

  // fetchRentReports(rentInfo: RentInfo): boolean {
  //   var returnValue = true;
  //   this.rentReportService.getAll().subscribe(
  //     (data: RentReport[]) => {
  //       for (var report of data) {
  //         if (report.rentInfo.id == rentInfo.id) {
  //           returnValue = false;

  //           return returnValue;
  //         }
  //       }
  //       returnValue = true;
  //     },
  //     (httpErrorResponse: HttpErrorResponse) => {
  //       const data: RentReport[] = [];
  //       this.toastr.error(httpErrorResponse.error.message, "Show Rent Reports");
  //     }
  //   );

  //   return returnValue;
  // }
}
