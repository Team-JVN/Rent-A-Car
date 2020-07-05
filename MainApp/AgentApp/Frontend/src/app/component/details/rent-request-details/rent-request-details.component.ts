import { UserInfo } from "src/app/model/userInfo";
import { MatDialog } from "@angular/material/dialog";
import { AuthentificationService } from "./../../../service/authentification.service";
import { RentInfo } from "./../../../model/rentInfo";
import { RentRequestService } from "./../../../service/rent-request.service";
import { RentRequest } from "./../../../model/rentRequest";
import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router, Params } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { HttpErrorResponse } from "@angular/common/http";
import { Location } from "@angular/common";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { Client } from "src/app/model/client";
import { Message } from "./../../../model/message";
import { MessageService } from "src/app/service/message.service";
import { AddRentReportComponent } from "../../add/add-rent-report/add-rent-report.component";
import { ReviewFeedbackComponent } from "../../review-feedback/review-feedback.component";
import { Comment } from "./../../../model/comment";
import { ListComments } from "../../list/list-comments/list-comments.component";
import { RentReportDetailsComponent } from "../rent-report-details/rent-report-details.component";

@Component({
  selector: "app-rent-request-details",
  templateUrl: "./rent-request-details.component.html",
  styleUrls: ["./rent-request-details.component.css"],
})
export class RentRequestDetailsComponent implements OnInit {
  messageForm: FormGroup;

  rentRequestId: number;
  rentRequest: RentRequest = new RentRequest(
    new Client("", "", "", ""),
    null,
    0,
    ""
  );
  loggedInUserEmail: string;
  availableRentReport: Map<number, boolean>;
  availableReviewFeedback: Map<number, boolean>;
  availableViewRentReport: Map<number, boolean>;
  availableViewComment: Map<number, boolean>;
  messages: Message[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private location: Location,
    private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.availableRentReport = new Map();
    this.availableReviewFeedback = new Map();
    this.availableViewRentReport = new Map();
    this.availableViewComment = new Map();

    this.messageForm = this.formBuilder.group({
      text: new FormControl(null, Validators.required),
    });
    this.activatedRoute.params.subscribe((params: Params) => {
      this.rentRequestId = params["id"];
      this.rentRequestService.get(this.rentRequestId).subscribe(
        (data: RentRequest) => {
          this.rentRequest = data;
          for (let rentInfo of this.rentRequest.rentInfos) {
            this.availableRentReport.set(rentInfo.id, true);
            this.availableReviewFeedback.set(rentInfo.id, true);
          }
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(
            httpErrorResponse.error.message,
            "Rent Request Details"
          );
          this.location.back();
        }
      );
    });
    this.loggedInUserEmail = this.authentificationService.getLoggedInUserEmail();
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(["/advertisement/" + rentInfo.advertisement.id]);
  }

  createRentReport(rentInfo: RentInfo) {
    this.availableRentReport.set(rentInfo.id, true);
    let dialogRef = this.dialog.open(AddRentReportComponent, {
      data: {
        rentReport: null,
        rentInfo: rentInfo,
        rentRequestId: this.rentRequestId,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result == true) {
        this.availableRentReport.set(rentInfo.id, false);
        this.availableViewRentReport.set(rentInfo.id, true);
      }
    });
  }

  reviewFeedback(rentInfo: RentInfo) {
    this.availableReviewFeedback.set(rentInfo.id, true);
    let dialogRef = this.dialog.open(ReviewFeedbackComponent, {
      data: {
        feedback: null,
        rentInfo: rentInfo,
        rentRequestId: this.rentRequestId,
      },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result == true) {
        this.availableReviewFeedback.set(rentInfo.id, false);
        this.availableViewComment.set(rentInfo.id, true);
      }
    });
  }

  checkIfCanCreateComment(rentInfo: RentInfo) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    let comm = rentInfo.comments[0] as Comment;
    if (
      this.rentRequest.rentRequestStatus == "PAID" &&
      dateTimeTo < new Date() &&
      rentInfo.comments.length > 0 &&
      rentInfo.comments.length < 2 &&
      comm.status == "APPROVED" &&
      this.availableReviewFeedback.get(rentInfo.id) != false
    ) {
      return true;
    }
    return false;
  }
  checkIfCanShowComments(rentInfo: RentInfo) {
    if (
      rentInfo.comments.length > 0 ||
      (rentInfo.rating != undefined && rentInfo.rating != 0) ||
      this.availableViewComment.get(rentInfo.id) == true
    ) {
      return true;
    }
    return false;
  }
  checkIfCanShowRentReport(rentInfo: RentInfo) {
    if (
      rentInfo.rentReport != null ||
      this.availableViewRentReport.get(rentInfo.id) == true
    ) {
      return true;
    }
    return false;
  }
  checkIfCanCreateReport(rentInfo: RentInfo) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (
      this.rentRequest.rentRequestStatus == "PAID" &&
      dateTimeTo < new Date() &&
      rentInfo.rentReport == null &&
      this.availableRentReport.get(rentInfo.id) != false
    ) {
      return true;
    }
    return false;
  }
  getMessages() {
    // this.messageService.getMessages(this.rentRequest.client).subscribe(
    //   (data: Message[]) => {
    //     this.toastr.success('Success!', 'Fetch messages');
    //     this.messages = data;
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     this.toastr.error(httpErrorResponse.error.message, 'Fetch messages');
    //   }
    // );
  }
  viewComments(rentInfo: RentInfo) {
    let dialogRef = this.dialog.open(ListComments, {
      data: {
        feedback: null,
        rentInfo: rentInfo,
        rentRequestId: this.rentRequestId,
      },
    });
  }
  viewRentReport(rentInfo: RentInfo) {
    let dialogRef = this.dialog.open(RentReportDetailsComponent, {
      data: {
        rentInfo: rentInfo,
      },
    });
  }
}
