import { RentInfo } from './../../../model/rentInfo';

import { LeaveFeedbackComponent } from './../../add/leave-feedback/leave-feedback.component';
import { RentRequestService } from 'src/app/service/rent-request.service';
import { RentRequest } from './../../../model/rentRequest';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { trigger, state, transition, style, animate } from '@angular/animations';
@Component({
  selector: 'app-list-client-rent-requests',
  templateUrl: './list-client-rent-requests.component.html',
  styleUrls: ['./list-client-rent-requests.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ListClientRentRequestsComponent implements OnInit {
  //CLIENT  HAVE ACCESS TO THIS PAGE
  displayedColumns: string[] = ['client', 'totalPrice', 'status', 'buttons'];
  expandedElement: RentRequest | null;
  rentRequestsDataSource: MatTableDataSource<RentRequest>;
  createSuccess: Subscription;
  status: string = "all";

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private rentRequestService: RentRequestService,
    private toastr: ToastrService
  ) { }

  ngOnInit() {
    /*   this.fetchRentRequests('all');
       this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
         () => {
           this.fetchRentRequests(this.status)
         }
       );*/
  }

  /*fetchRentRequests(status: string) {
    this.rentRequestService.getClientRentRequests(status).subscribe(
      (data: RentRequest[]) => {
        this.rentRequestsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: RentRequest[] = []
        this.rentRequestsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Rent Requests');

      }
    );
  }

  delete(element: RentRequest) {
    this.rentRequestService.delete(element.id).subscribe(
      () => {
        this.fetchRentRequests(this.status);
        this.toastr.success('Successfully deleted Rent Request!', 'Delete Rent Request');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Rent Request');
      }
    );
  }

  cancel(element: RentRequest) {
    this.rentRequestService.cancel(element.id).subscribe(
      () => {
        this.fetchRentRequests(this.status);
        this.toastr.success('Successfully canceled Rent Request!', 'Cancel Rent Request');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Cancel Rent Request');
      }
    );
  }
  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(['/advertisement/' + rentInfo.advertisement.id]);
  }

  viewDetails(element: RentRequest) {
    this.router.navigate(['/client-rent-request/' + element.id]);
  }

  checkIfCanLeaveFeedback(rentInfo: RentInfo, rentRequest: RentRequest) {
    const dateTimeTo = new Date(rentInfo.dateTimeTo.substring(0, 10));
    if (rentRequest.rentRequestStatus == 'PAID' && dateTimeTo <= new Date()) {
      return true;
    }
    return false;
  }

  leaveFeedback(rentInfo, element) {
    this.dialog.open(LeaveFeedbackComponent, { data: { rentInfo: rentInfo, rentRequest: element } });
  }*/
}
