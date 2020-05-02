import { RentRequestService } from 'src/app/service/rent-request.service';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { LoggedInUser } from './../../model/loggedInUser';
import { Comment } from './../../model/comment';
import { Feedback } from './../../model/feedback';
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormControl, Validators, FormGroup } from '@angular/forms';
import { runInThisContext } from 'vm';

@Component({
  selector: 'app-review-feedback',
  templateUrl: './review-feedback.component.html',
  styleUrls: ['./review-feedback.component.css']
})
export class ReviewFeedbackComponent implements OnInit {
  setUpComment: Comment;
  loggedInUser: LoggedInUser;
  loggedInUserComment: Comment;
  commentForm: FormGroup;

  constructor(private dialogRef: MatDialogRef<ReviewFeedbackComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any, private toastr: ToastrService, private rentRequestService: RentRequestService) { }

  ngOnInit() {
    const comments = [new Comment("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")];
    this.data.feedback = new Feedback(4, comments);
    console.log(this.data)
    // this.data.feedback.comments.forEach(comment => {
    //   if (comment.userInfo.email != this.loggedInUser.email) {
    //     this.setUpComment = comment;
    //   } else {
    //     this.loggedInUserComment = comment;
    //   }
    // });

    this.commentForm = this.formBuilder.group({
      comment: new FormControl(null, Validators.required)
    })

    this.setUpComment = new Comment("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
    //this.loggedInUserComment = new Comment("1.Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
  }


  createComment() {

    const comment = new Comment(this.commentForm.value.comment);

    this.rentRequestService.createComment(comment, this.data.rentInfoId, this.data.rentRequestId).subscribe(
      (data: Comment) => {
        this.toastr.success('Success!', 'Create comment');
        this.dialogRef.close();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create comment');
      }
    );
  }
}
