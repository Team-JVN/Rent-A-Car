import { RentRequestService } from "src/app/service/rent-request.service";
import { HttpErrorResponse } from "@angular/common/http";
import { ToastrService } from "ngx-toastr";
import { LoggedInUser } from "./../../model/loggedInUser";
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
    // const comments = [new Comment("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")];
    // this.data.feedback = new Feedback(4, comments);
    this.loggedInUser = this.authentificationService.getLoggedInUserEmail();
    console.log(this.data.rentInfo.id);
    console.log(this.data.rentInfo);

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
          console.log(data);
          this.feedback.comments.forEach((comment) => {
            console.log(comment);
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
    console.log(this.commentForm.value.comment);
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
          this.router.navigate(["/rent-request/" + this.data.rentInfo.id]);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Create comment");
        }
      );
  }
}
