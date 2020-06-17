import { CarWithPictures } from "src/app/model/carWithPictures";
import { AdvertisementWithPictures } from "src/app/model/advertisementWithPictures";
import { LeaveFeedbackComponent } from "./../../add/leave-feedback/leave-feedback.component";

import { UserInfo } from "src/app/model/userInfo";
import { MatDialog } from "@angular/material/dialog";
import { Feedback } from "./../../../model/feedback";
import { MessageService } from "./../../../service/message.service";
import { Message } from "./../../../model/message";
import { AuthentificationService } from "./../../../service/authentification.service";
import { LoggedInUser } from "./../../../model/loggedInUser";
import { RentInfo } from "./../../../model/rentInfo";
import { Comment } from "./../../../model/comment";
import { Advertisement } from "./../../../model/advertisement";
import { Car } from "src/app/model/car";
import { Model } from "./../../../model/model";
import { Make } from "./../../../model/make";
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
import { CommentService } from "src/app/service/comment.service";

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
    [],
    0,
    ""
  );
  loggedInUserEmail: string;
  messages: Message[];
  availibleLeavingFeedback: boolean;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private location: Location,
    private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder,
    private messageService: MessageService,
    public dialog: MatDialog,
    private commentService: CommentService
  ) {}

  ngOnInit() {
    this.messageForm = this.formBuilder.group({
      text: new FormControl(null, Validators.required),
    });
    this.activatedRoute.params.subscribe((params: Params) => {
      this.rentRequestId = params["id"];
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
    });
    this.fetchComments();
    this.loggedInUserEmail = this.authentificationService.getLoggedInUserEmail();
    // this.getMessages();

    //Delete this
    // this.messages = [new Message("Cao sta radi,kako si, da li si dorbo.Kako su tvoji. sta radis", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("Kako si", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2),
    // new Message("Dobro", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("To?", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2)];
    // this.rentRequestId = 2;
  }
  fetchComments() {
    // this.rentRequestService
    //   .getRentInfoFeedback(this.data.rentInfo.id, this.data.rentRequest.id)
    //   .subscribe(
    //     (data: Feedback) => {
    //       console.log("FEEDBACK");
    //       console.log(data);
    //       this.toastr.success("Success!", "Fetch feedback");
    //     },
    //     (httpErrorResponse: HttpErrorResponse) => {
    //       this.toastr.error(httpErrorResponse.error.message, "Fetch feedback");
    //     }
    //   );
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(["/advertisement/" + rentInfo.advertisement.id]);
  }

  getMessages() {
    this.messageService.getMessages(this.rentRequestId).subscribe(
      (data: Message[]) => {
        this.toastr.success("Success!", "Fetch messages");
        this.messages = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Fetch messages");
      }
    );
  }

  checkIfCanLeaveFeedback(rentInfo: RentInfo, rentRequest: RentRequest) {
    console.log("rentInfo*****");
    console.log(rentInfo);

    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (
      rentRequest.rentRequestStatus == "PAID" &&
      dateTimeTo < new Date() &&
      rentInfo.comments.length < 1 &&
      this.availibleLeavingFeedback != false
    ) {
      return true;
    }
    return false;
  }

  leaveFeedback(rentInfo: RentInfo) {
    this.availibleLeavingFeedback = true;
    let dialogRef = this.dialog.open(LeaveFeedbackComponent, {
      data: { rentInfo: rentInfo, rentRequest: this.rentRequest },
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result == true) {
        this.availibleLeavingFeedback = false;
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
}
