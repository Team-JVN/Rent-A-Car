import { RentRequestService } from "src/app/service/rent-request.service";
import { HttpErrorResponse } from "@angular/common/http";
import { ToastrService } from "ngx-toastr";
import { Comment } from "./../../model/comment";
import { Feedback } from "./../../model/feedback";
import { Component, OnInit, Inject } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import {
  FormBuilder,
  FormControl,
  Validators,
  FormGroup,
} from "@angular/forms";
import { runInThisContext } from "vm";
import { AuthentificationService } from "src/app/service/authentification.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-review-feedback",
  templateUrl: "./review-feedback.component.html",
  styleUrls: ["./review-feedback.component.css"],
})
export class ReviewFeedbackComponent implements OnInit {
  setUpComment: Comment;
  loggedInUser;
  loggedInUserComment: Comment;
  commentForm: FormGroup;
  comments: [];
  feedback: Feedback;

  constructor(
    private dialogRef: MatDialogRef<ReviewFeedbackComponent>,
    private formBuilder: FormBuilder,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private authentificationService: AuthentificationService,
    public router: Router,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.loggedInUser = this.authentificationService.getLoggedInUserEmail();

    this.fetchFeedback();

    this.commentForm = this.formBuilder.group({
      comment: new FormControl(null, Validators.required),
    });
  }

  fetchFeedback() {
    this.rentRequestService
      .getRentInfoFeedback(this.data.rentInfo.id, this.data.rentRequestId)
      .subscribe(
        (data: Feedback) => {
          this.feedback = data;

          this.feedback.comments.forEach((comment) => {
            if (comment.sender.name != this.loggedInUser.name) {
              this.setUpComment = comment;
            }
          });
          this.toastr.success("Success!", "Fetch feedback");
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Fetch feedback");
        }
      );
  }

  createComment() {
    const comment = new Comment(
      this.commentForm.value.comment,
      this.data.rentInfo
    );

    this.rentRequestService
      .createComment(comment, this.data.rentInfo.id, this.data.rentRequestId)
      .subscribe(
        (data: Comment) => {
          this.toastr.success("Success!", "Create comment");
          this.dialogRef.close(true);
          this.router.navigate(["/rent-request/" + this.data.rentRequestId]);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Create comment");
        }
      );
  }
}
