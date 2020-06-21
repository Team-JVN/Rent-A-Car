import { UserInfo } from "src/app/model/userInfo";
import { MatDialog } from "@angular/material/dialog";
import { MessageService } from "./../../../service/message.service";
import { Message } from "./../../../model/message";
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
import { ReviewFeedbackComponent } from "../../review-feedback/review-feedback.component";
import { AddRentReportComponent } from "../../add/add-rent-report/add-rent-report.component";
import { Comment } from "./../../../model/comment";

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
    [],
    0,
    ""
  );
  loggedInUserEmail: string;
  availableRentReport: Map<number, boolean>;
  availableReviewFeedback: Map<number, boolean>;
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

    // this.getMessages();

    //Delete this
    // this.messages = [new Message("Cao sta radi,kako si, da li si dorbo.Kako su tvoji. sta radis", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("Kako si", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2),
    // new Message("Dobro", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("To?", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2)];
    // this.rentRequestId = 2;
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
      }
    });
  }

  checkIfCanCreateComment(rentInfo: RentInfo) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    let comment = new Comment("", new UserInfo(this.loggedInUserEmail));
    let comm = rentInfo.comments[0] as Comment;
    if (rentInfo.comments.length < 1) {
      return false;
    }
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
    this.messageService.getMessages(this.rentRequest.id).subscribe(
      (data: Message[]) => {
        this.toastr.success("Success!", "Fetch messages");
        this.messages = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Fetch messages");
      }
    );
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
}
