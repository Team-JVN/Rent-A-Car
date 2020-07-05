import { UserInfo } from "src/app/model/userInfo";
import { MatDialog, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { MessageService } from "./../../../service/message.service";
import { Message } from "./../../../model/message";
import { AuthentificationService } from "./../../../service/authentification.service";
import { RentInfo } from "./../../../model/rentInfo";
import { RentRequestService } from "./../../../service/rent-request.service";
import { RentRequest } from "./../../../model/rentRequest";
import { Component, OnInit, Inject } from "@angular/core";
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
import { Feedback } from "src/app/model/feedback";

@Component({
  selector: "app-list-comments",
  templateUrl: "./list-comments.component.html",
  styleUrls: ["./list-comments.component.css"],
})
export class ListComments implements OnInit {
  setUpComment: Comment;
  loggedInUserEmail;
  loggedInUserComment: Comment;
  comments: [];
  feedback: Feedback;

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
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.loggedInUserEmail = this.authentificationService.getLoggedInUserEmail();
    this.fetchFeedback();
  }
  fetchFeedback() {
    this.rentRequestService
      .getRentInfoFeedback(this.data.rentInfo.id, this.data.rentRequestId)
      .subscribe(
        (data: Feedback) => {
          this.feedback = data;

          this.feedback.comments.forEach((comment) => {
            if (comment.sender.email != this.loggedInUserEmail) {
              this.setUpComment = comment;
            } else {
              this.loggedInUserComment = comment;
            }
          });
          this.toastr.success("Success!", "Fetch feedback");
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Fetch feedback");
        }
      );
  }
}
