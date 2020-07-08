import { HttpErrorResponse } from "@angular/common/http";
import { Feedback } from "./../../../model/feedback";
import { RentRequestService } from "src/app/service/rent-request.service";
import { ToastrService } from "ngx-toastr";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Component, OnInit, Inject } from "@angular/core";
import { Comment } from "src/app/model/comment";
import { AuthentificationService } from "src/app/service/authentification.service";
import { UserInfo } from "src/app/model/userInfo";

@Component({
  selector: "app-leave-feedback",
  templateUrl: "./leave-feedback.component.html",
  styleUrls: ["./leave-feedback.component.css"],
})
export class LeaveFeedbackComponent implements OnInit {
  rating: Number;
  comment: string;
  stars: number[] = [1, 2, 3, 4, 5];
  loggedInUser;

  constructor(
    private dialogRef: MatDialogRef<LeaveFeedbackComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private authentificationService: AuthentificationService
  ) { }

  ngOnInit() {
    this.loggedInUser = this.authentificationService.getLoggedInUserEmail();
    this.rentRequestService
      .getRentInfoFeedback(this.data.rentInfo.id, this.data.rentRequest.id)
      .subscribe(
        (data: Feedback) => {
          this.toastr.success("Success!", "Fetch feedback");
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Fetch feedback");
        }
      );
  }

  setRating(star: number) {
    this.rating = star;
  }

  leaveFeedback() {
    const feedback = new Feedback(this.rating, [
      new Comment(this.comment, new UserInfo(this.loggedInUser)),
    ]);

    this.rentRequestService
      .leaveFeedback(feedback, this.data.rentInfo.id, this.data.rentRequest.id)
      .subscribe(
        (data: Feedback) => {
          this.toastr.success("Success!", "Leave feedback");
          this.dialogRef.close(true);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Leave feedback");
          this.dialogRef.close();
        }
      );
  }
}
