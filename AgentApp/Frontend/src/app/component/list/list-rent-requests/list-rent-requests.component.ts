import { EditRentRequestComponent } from './../../edit/edit-rent-request/edit-rent-request.component';
import { RentInfo } from './../../../model/rentInfo';
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
  selector: 'app-list-rent-requests',
  templateUrl: './list-rent-requests.component.html',
  styleUrls: ['./list-rent-requests.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class ListRentRequestsComponent implements OnInit {
  displayedColumns: string[] = ['client', 'totalPrice', 'buttons'];
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
    this.fetchRentRequests('all');
    this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
      () => {
        this.fetchRentRequests(this.status)
      }
    );
  }

  fetchRentRequests(status: string) {
    this.rentRequestService.getRentRequests(status).subscribe(
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

  edit(element: RentRequest) {
    this.dialog.open(EditRentRequestComponent, { data: element });

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

  createRentReport(rentRequest: RentRequest, rentInfo: RentInfo) {
    // this.dialog.open(AddRentRequestComponent, { data: element.advertisement });
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(['/advertisement/' + rentInfo.advertisement.id]);
  }
}
