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
import { Message } from "./../../../model/message";
import { Client } from "src/app/model/client";
import { MessageService } from "src/app/service/message.service";
import { LeaveFeedbackComponent } from "../../add/leave-feedback/leave-feedback.component";
import { ListComments } from "../../list/list-comments/list-comments.component";

@Component({
  selector: "app-client-rent-request-details",
  templateUrl: "./client-rent-request-details.component.html",
  styleUrls: ["./client-rent-request-details.component.css"],
})
export class ClientRentRequestDetailsComponent implements OnInit {
  messageForm: FormGroup;
  rentRequestId: number;
  rentRequest: RentRequest = new RentRequest(
    new Client("", "", "", ""),
    null,
    0,
    ""
  );
  loggedInUserEmail: string;
  messages: Message[];
  availibleLeavingFeedback: Map<number, boolean>;
  availableViewComment: Map<number, boolean>;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private location: Location,
    private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private messageService: MessageService
  ) {}

  ngOnInit() {
    this.availibleLeavingFeedback = new Map();
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
            this.availibleLeavingFeedback.set(rentInfo.id, true);
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

  checkIfCanLeaveFeedback(rentInfo: RentInfo, rentRequest: RentRequest) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (
      rentRequest.rentRequestStatus == "PAID" &&
      dateTimeTo < new Date() &&
      rentInfo.comments.length < 1 &&
      this.availibleLeavingFeedback.get(rentInfo.id) != false &&
      (rentInfo.rating == 0 || rentInfo.rating == undefined)
    ) {
      return true;
    }
    return false;
  }

  leaveFeedback(rentInfo: RentInfo) {
    this.availibleLeavingFeedback.set(rentInfo.id, true);
    let dialogRef = this.dialog.open(LeaveFeedbackComponent, {
      data: { rentInfo: rentInfo, rentRequest: this.rentRequest },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result == true) {
        this.availibleLeavingFeedback.set(rentInfo.id, false);
        this.availableViewComment.set(rentInfo.id, true);
      }
    });
  }
  pay(rentInfo: RentInfo, rentRequest: RentRequest) {
    this.rentRequestService.pay(rentRequest.id, rentInfo.id).subscribe(
      () => {
        this.toastr.success("Success", "Pay Rent Info");
        this.fetchRentRequest();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Pay Rent Info");
      }
    );
  }

  fetchRentRequest() {
    this.rentRequestService.get(this.rentRequestId).subscribe(
      (data: RentRequest) => {
        this.rentRequest = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(
          httpErrorResponse.error.message,
          "Rent Request Details"
        );
        this.location.back();
      }
    );
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
  viewComments(rentInfo: RentInfo) {
    let dialogRef = this.dialog.open(ListComments, {
      data: {
        feedback: null,
        rentInfo: rentInfo,
        rentRequestId: this.rentRequestId,
      },
    });
  }
}
