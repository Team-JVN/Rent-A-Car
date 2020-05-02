import { LeaveFeedbackComponent } from './../../add/leave-feedback/leave-feedback.component';
import { Car } from 'src/app/model/car';
import { BodyStyle } from './../../../model/bodystyle';
import { GearBoxType } from './../../../model/gearboxType';
import { FuelType } from './../../../model/fuelType';
import { Model } from './../../../model/model';
import { Make } from 'src/app/model/make';
import { Advertisement } from './../../../model/advertisement';
import { element } from 'protractor';
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
import { PriceList } from 'src/app/model/priceList';
import { Client } from 'src/app/model/client';
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
    const data1: RentRequest[] = []
    const make = new Make("Opel", 1);
    const model = new Model("Poze");
    const car = new Car(make, model, new FuelType("fuel"), new GearBoxType("gear"), null, 1000, 2, true);
    const advestisement = new Advertisement(car, new PriceList(1, 2, 2), 20, 2500, true, "2020-05-05", true);
    const rentInfo = new RentInfo("2020-05-05", "2020-05-05", "Beograd", true, advestisement);
    const rentInfos = [];
    rentInfos.push(rentInfo)
    data1.push(new RentRequest(new Client("pera", "pera@uns.ac.rs", "Beograd", "066666666"), rentInfos, 200, "PAID"))
    this.rentRequestsDataSource = new MatTableDataSource(data1);
    //   this.fetchRentRequests('all');
    this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
      () => {
        this.fetchRentRequests(this.status)
      }
    );
  }

  fetchRentRequests(status: string) {
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
  }
}
