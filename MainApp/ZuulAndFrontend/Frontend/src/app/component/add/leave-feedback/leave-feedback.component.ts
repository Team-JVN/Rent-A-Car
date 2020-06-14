import { HttpErrorResponse } from "@angular/common/http";
import { Feedback } from "./../../../model/feedback";
import { RentRequestService } from "src/app/service/rent-request.service";
import { ToastrService } from "ngx-toastr";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { Component, OnInit, Inject } from "@angular/core";
import { Comment } from "src/app/model/comment";

@Component({
  selector: "app-leave-feedback",
  templateUrl: "./leave-feedback.component.html",
  styleUrls: ["./leave-feedback.component.css"],
})
export class LeaveFeedbackComponent implements OnInit {
  rating: Number;
  comment: string;
  stars: number[] = [1, 2, 3, 4, 5];

  constructor(
    private dialogRef: MatDialogRef<LeaveFeedbackComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService
  ) {}

  ngOnInit() {
    this.rentRequestService
      .getRentInfoFeedback(this.data.rentInfo.id, this.data.rentRequest.id)
      .subscribe(
        (data: Feedback) => {
          console.log(data);
          this.toastr.success("Success!", "Fetch feedback");
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Fetch feedback");
        }
      );
  }

  setRating(star: number) {
    this.rating = star;
    console.log("RATING: " + star);
  }

  leaveFeedback() {
    const feedback = new Feedback(this.rating, [
      new Comment(this.comment, this.data.rentInfo),
    ]);
    console.log(feedback);
    this.rentRequestService
      .leaveFeedback(feedback, this.data.rentInfo.id, this.data.rentRequest.id)
      .subscribe(
        (data: Feedback) => {
          this.toastr.success("Success!", "Leave feedback");
          this.dialogRef.close();
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Leave feedback");
        }
      );
  }
}
