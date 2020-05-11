import { UserInfo } from 'src/app/model/userInfo';
import { MatDialog } from '@angular/material/dialog';
import { AuthentificationService } from './../../../service/authentification.service';
import { RentInfo } from './../../../model/rentInfo';
import { RentRequestService } from './../../../service/rent-request.service';
import { RentRequest } from './../../../model/rentRequest';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { Location } from '@angular/common';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-rent-request-details',
  templateUrl: './rent-request-details.component.html',
  styleUrls: ['./rent-request-details.component.css']
})
export class RentRequestDetailsComponent implements OnInit {
  messageForm: FormGroup;

  rentRequestId: number;
  rentRequest: RentRequest;
  loggedInUserEmail: string;

  // messages: Message[];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private location: Location,
    private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
  ) { }

  ngOnInit() {
    this.messageForm = this.formBuilder.group({
      text: new FormControl(null, Validators.required)
    })
    this.activatedRoute.params.subscribe((params: Params) => {
      this.rentRequestId = params['id'];
      this.rentRequestService.get(this.rentRequestId).subscribe(
        (data: RentRequest) => {
          this.rentRequest = data;
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, 'Rent Request Details');
          this.location.back();
        }
      )
    });
    this.loggedInUserEmail = this.authentificationService.getLoggedInUser().email;
    // this.getMessages();

    // //Delete this
    // this.messages = [new Message("Cao sta radi,kako si, da li si dorbo.Kako su tvoji. sta radis", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("Kako si", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2),
    // new Message("Dobro", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("To?", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2)];
    // this.rentRequestId = 2;
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(['/advertisement/' + rentInfo.advertisement.id]);
  }

  createRentReport() {

  }

  reviewFeedback(rentInfo: RentInfo) {
    // this.rentRequestService.getRentInfoFeedback(this.rentInfo.id).subscribe(
    //   (feedback: Feedback) => {
    //     if (feedback.rating) {
    //       this.dialog.open(ReviewFeedbackComponent, { data: { feedback: feedback, rentInfoId: rentInfo.id, rentRequestId: this.rentRequestId }});
    //     }
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     this.toastr.error(httpErrorResponse.error.message, 'Review feedback');
    //   }
    // );

    //this.dialog.open(ReviewFeedbackComponent, { data: { feedback: null, rentInfoId: rentInfo.id, rentRequestId: this.rentRequestId } });
  }

  checkIfCanCreateComment(rentInfo: RentInfo) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (this.rentRequest.rentRequestStatus == 'PAID' && dateTimeTo < new Date()) {
      return true;
    }
    return false;
  }

  checkIfCanCreateReport(rentInfo: RentInfo) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (this.rentRequest.rentRequestStatus == 'PAID' && dateTimeTo < new Date()) {
      return true;
    }
    return false;
  }
  // getMessages() {
  //   this.messageService.getMessages(this.rentRequest.client).subscribe(
  //     (data: Message[]) => {
  //       this.toastr.success('Success!', 'Fetch messages');
  //       this.messages = data;
  //     },
  //     (httpErrorResponse: HttpErrorResponse) => {
  //       this.toastr.error(httpErrorResponse.error.message, 'Fetch messages');
  //     }
  //   );
  // }
}