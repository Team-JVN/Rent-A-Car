import { AuthentificationService } from "./../../service/authentification.service";

import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { ToastrService } from "ngx-toastr";
import { MatDialog } from "@angular/material/dialog";
import { Comment } from "./../../model/comment";
import { Subscription } from "rxjs";
import { MatTableDataSource } from "@angular/material/table";
import { HttpErrorResponse } from "@angular/common/http";
import { RejectRequestToRegisterComponent } from "../reject-request-to-register/reject-request-to-register.component";

import { CommentService } from "src/app/service/comment.service";

@Component({
  selector: "app-manage-comments",
  templateUrl: "./manage-comments.component.html",
  styleUrls: ["./manage-comments.component.css"],
})
export class ManageCommentsComponent implements OnInit {
  status: string = "all";
  approveCommentSuccess: Subscription;
  rejectCommentSuccess: Subscription;

  commentsDataSource: MatTableDataSource<Comment>;

  displayedColumnsComment: string[] = ["comment", "sender", "buttons"];

  constructor(
    private toastr: ToastrService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    public authentificationService: AuthentificationService,
    public commentService: CommentService
  ) {}

  ngOnInit() {
    this.fetchAllComments("all");

    this.approveCommentSuccess = this.commentService.approveSuccessEmitter.subscribe(
      () => {
        this.fetchAllComments("all");
      }
    );

    this.rejectCommentSuccess = this.commentService.rejectSuccessEmitter.subscribe(
      () => {
        this.fetchAllComments("all");
      }
    );
  }

  fetchAllComments(status: string) {
    this.commentService.getAll(status).subscribe(
      (data: Comment[]) => {
        this.commentsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Comment[] = [];
        this.commentsDataSource = new MatTableDataSource(data);
        this.toastr.error(httpErrorResponse.error.message, "Show comments");
      }
    );
  }

  reject(element: Comment) {
    this.commentService.reject(element, element.id).subscribe(
      () => {
        this.toastr.success("Success.", "Reject comment");
        this.fetchAllComments(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Reject comment");
      }
    );
  }

  approve(element: Comment) {
    this.commentService.approve(element, element.id).subscribe(
      () => {
        this.toastr.success("Success.", "Approve comment");
        this.fetchAllComments(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Approve comment");
      }
    );
  }
}
