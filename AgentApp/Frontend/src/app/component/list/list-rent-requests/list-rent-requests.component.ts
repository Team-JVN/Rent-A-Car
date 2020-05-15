import { AddRentReportComponent } from './../../add/add-rent-report/add-rent-report.component';
import { AdvertisementService } from 'src/app/service/advertisement.service';
import { RentInfo } from './../../../model/rentInfo';
import { RentRequestService } from 'src/app/service/rent-request.service';
import { RentRequest } from './../../../model/rentRequest';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { Router, ActivatedRoute, Params } from '@angular/router';
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

  displayedColumns: string[] = ['client', 'totalPrice', 'status', 'buttons'];
  expandedElement: RentRequest | null;
  rentRequestsDataSource: MatTableDataSource<RentRequest>;
  createSuccess: Subscription;
  status: string = "all";
  advertisementId: number;

  constructor(
    private activatedRoute: ActivatedRoute,
    public router: Router,
    public dialog: MatDialog,
    private advertisementService: AdvertisementService,
    private rentRequestService: RentRequestService,
    private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.advertisementId = params['id'];
      this.fetchRentRequests('all');
    });
    this.fetchRentRequests('all');
    this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
      () => {
        this.fetchRentRequests(this.status)
      }
    );
  }

  fetchRentRequests(status: string) {
    this.advertisementService.getRentRequests(this.advertisementId, status).subscribe(
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

  createRentReport(rentRequest: RentRequest, rentInfo: RentInfo) {
    this.dialog.open(AddRentReportComponent, { data: rentInfo });
  }

  advertisementDetails(rentInfo: RentInfo) {
    this.router.navigate(['/advertisement/' + rentInfo.advertisement.id]);
  }

  viewDetails(element: RentRequest) {
    this.router.navigate(['/rent-request/' + element.id]);
  }

  accept(element: RentRequest) {
    this.rentRequestService.accept(element).subscribe(
      () => {
        this.fetchRentRequests(this.status);
        this.toastr.success('Successfully accepted Rent Request!', 'Accept Rent Request');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Accept Rent Request');
      }
    );
  }

  reject(element: RentRequest) {
    this.rentRequestService.reject(element).subscribe(
      () => {
        this.fetchRentRequests(this.status);
        this.toastr.success('Successfully canceled Rent Request!', 'Cancel Rent Request');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Cancel Rent Request');
      }
    );
  }
}
