import { LeaveFeedbackComponent } from './../../add/leave-feedback/leave-feedback.component';

import { UserInfo } from 'src/app/model/userInfo';
import { MatDialog } from '@angular/material/dialog';
import { Feedback } from './../../../model/feedback';
import { MessageService } from './../../../service/message.service';
import { Message } from './../../../model/message';
import { AuthentificationService } from './../../../service/authentification.service';
import { LoggedInUser } from './../../../model/loggedInUser';
import { RentInfo } from './../../../model/rentInfo';
import { Advertisement } from './../../../model/advertisement';
import { Car } from 'src/app/model/car';
import { Model } from './../../../model/model';
import { Make } from './../../../model/make';
import { RentRequestService } from './../../../service/rent-request.service';
import { RentRequest } from './../../../model/rentRequest';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { Location } from '@angular/common';
import { FuelType } from 'src/app/model/fuelType';
import { GearBoxType } from 'src/app/model/gearboxType';
import { Client } from 'src/app/model/client';
import { PriceList } from 'src/app/model/priceList';
import { MatTableDataSource } from '@angular/material/table';
import { User } from 'src/app/model/user';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-client-rent-request-details',
  templateUrl: './client-rent-request-details.component.html',
  styleUrls: ['./client-rent-request-details.component.css']
})
export class ClientRentRequestDetailsComponent implements OnInit {

  messageForm: FormGroup;

  rentRequestId: number;
  // rentRequest: RentRequest;
  loggedInUserEmail: string;

  messages: Message[];
  make = new Make("Opel", 1);
  model = new Model("Poze", 2);
  car = new Car(this.make, this.model, new FuelType("fuel"), new GearBoxType("gear"), null, 1000, 2, true);
  advestisement = new Advertisement(this.car, new PriceList(1, 2, 2), 20, 2500, true, "2020-05-05", true);
  rentInfo = new RentInfo("2020-04-04", "2020-04-04", "Beograd", true, this.advestisement, 1);
  rentInfos = [new RentInfo("2020-04-04", "2020-04-04", "Beograd", true, this.advestisement, 2),
  new RentInfo("2020-05-05", "2020-05-05", "Beograd", true, this.advestisement)];

  rentRequest = new RentRequest(new Client("Pera", "pera@uns.ac.rs", "Beograd", "066666666"), this.rentInfos, 200, "PAID");

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
  ) { }

  ngOnInit() {
    this.messageForm = this.formBuilder.group({
      text: new FormControl(null, Validators.required)
    })
    // this.activatedRoute.params.subscribe((params: Params) => {
    //   this.rentRequestId = params['id'];
    //   this.rentRequestService.get(this.rentRequestId).subscribe(
    //     (data: RentRequest) => {
    //       this.rentRequest = data;
    //     },
    //     (httpErrorResponse: HttpErrorResponse) => {
    //       this.toastr.error(httpErrorResponse.error.message, 'Rent Request Details');
    //       this.location.back();
    //     }
    //   )
    // });
    // this.loggedInUserEmail = this.authentificationService.getLoggedInUser().email;
    //this.getMessages();

    //Delete this
    this.messages = [new Message("Cao sta radi,kako si, da li si dorbo.Kako su tvoji. sta radis", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("Kako si", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2),
    new Message("Dobro", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 1), new Message("To?", new UserInfo("pera@gamil.com", "Miroslav Mirosavljevic"), 2)];
    this.rentRequestId = 2;
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(['/advertisement/' + rentInfo.advertisement.id]);
  }

  getMessages() {
    this.messageService.getMessages(this.rentRequest.client).subscribe(
      (data: Message[]) => {
        this.toastr.success('Success!', 'Fetch messages');
        this.messages = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Fetch messages');
      }
    );
  }

  checkIfCanLeaveFeedback(rentInfo: RentInfo, rentRequest: RentRequest) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (rentRequest.rentRequestStatus == 'PAID' && dateTimeTo <= new Date()) {
      return true;
    }
    return false;
  }

  leaveFeedback(rentInfo: RentInfo) {
    this.dialog.open(LeaveFeedbackComponent, { data: { rentInfo: rentInfo, rentRequest: this.rentRequest } });
  }
}
