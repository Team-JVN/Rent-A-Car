import { RentRequest } from './../../model/rentRequest';
import { HttpErrorResponse } from '@angular/common/http';
import { RentRequestService } from './../../service/rent-request.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { UserInfo } from './../../model/userInfo';
import { Subscription } from 'rxjs';
import { RentInfo } from './../../model/rentInfo';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-renting-cart',
  templateUrl: './renting-cart.component.html',
  styleUrls: ['./renting-cart.component.css']
})
export class RentingCartComponent implements OnInit {

  displayedColumns: string[] = ['rentInfo'];
  rentInfosDataSource: MatTableDataSource<RentInfo>;
  createSuccess: Subscription;
  rentInfos: RentInfo[] = [];
  owners: UserInfo[] = [];
  owner: UserInfo;
  allOwners: UserInfo;

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
  ) { }

  ngOnInit() {
    this.allOwners = new UserInfo('- All -', '- All -');
    this.owner = this.allOwners;
    this.fetchRentInfos();
  }

  fetchRentInfos() {
    this.rentInfos = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    let idx = 0;
    this.rentInfos.forEach(rentInfo => {
      rentInfo.index = idx;
      idx += 1;
    })
    localStorage.setItem("rentInfos", JSON.stringify(this.rentInfos));

    this.owners = Object.values(this.rentInfos.reduce(
      function (map, item) {
        map[item.advertisement.car.owner.email] = item.advertisement.car.owner;
        return map;
      }, {}));
    this.rentInfosDataSource = new MatTableDataSource(this.rentInfos);
  }

  fetchFromOwner() {
    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    if (this.owner !== this.allOwners) {
      rentInfos = rentInfos.filter(item => item.advertisement.car.owner.email === this.owner.email);
    }
    this.rentInfosDataSource = new MatTableDataSource(rentInfos);
  }

  changeChbValue(idx: number) {
    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    rentInfos[idx].inBundle = !rentInfos[idx].inBundle;
    localStorage.setItem("rentInfos", JSON.stringify(rentInfos));
  }

  createRentRequest() {
    this.rentInfos = [];
    this.rentInfosDataSource = new MatTableDataSource(this.rentInfos);
    let rentInfosCopy: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    localStorage.setItem("rentInfos", JSON.stringify(this.rentInfos));

    let bundleRequests = new Map<string, RentRequest>();
    this.owners.forEach(owner => {
      bundleRequests.set(owner.email, new RentRequest(null, []));
    });

    rentInfosCopy.forEach(item => {
      if (item.inBundle) {
        bundleRequests.get(item.advertisement.car.owner.email).rentInfos.push(item);
      } else {
        this.rentRequestService.create(new RentRequest(null, [item])).subscribe(
          () => { this.toastr.success('Rent requests is successfully sent!', 'Create Rent Request(s)'); },
          (httpErrorResponse: HttpErrorResponse) => {
            this.rentInfos.push(item);
            this.rentInfosDataSource = new MatTableDataSource(this.rentInfos);
            localStorage.setItem("rentInfos", JSON.stringify(this.rentInfos));
            this.toastr.error(httpErrorResponse.error.message, 'Create Rent Request(s)');
          }
        );
      }
    })

    for (let [_, req] of bundleRequests) {
      if (req.rentInfos.length > 0) {
        this.rentRequestService.create(req).subscribe(
          () => {
            this.toastr.success('Rent requests is successfully sent!', 'Create Rent Request(s)');
          },
          (httpErrorResponse: HttpErrorResponse) => {
            req.rentInfos.forEach(item => this.rentInfos.push(item));
            this.rentInfosDataSource = new MatTableDataSource(this.rentInfos);
            localStorage.setItem("rentInfos", JSON.stringify(this.rentInfos));
            this.toastr.error(httpErrorResponse.error.message, 'Create Rent Request(s)');
          }
        );
      }
    }

  }

  emptyCart() {
    localStorage.removeItem("rentInfos");
    this.fetchRentInfos();
  }

  removeItem(idx: number) {
    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    if (idx != -1) {
      rentInfos.splice(idx, 1);
    }
    localStorage.setItem("rentInfos", JSON.stringify(rentInfos));
    this.fetchRentInfos();
  }

  calculatePrice(element: RentInfo) {
    const dateTimeFrom = moment(element.dateTimeFrom);
    const dateTimeTo = moment(element.dateTimeTo);
    const daysBetween = Math.ceil(dateTimeTo.diff(dateTimeFrom, 'days', true));

    let calculatedPrice = 0;
    if (element.optedForCDW) {
      calculatedPrice += element.advertisement.priceList.priceForCDW * daysBetween;
    }
    if (element.advertisement.discount && daysBetween > 30) {
      const reducedPricePerDay = element.advertisement.priceList.pricePerDay * (1.0 - (element.advertisement.discount / 100.0));
      calculatedPrice += reducedPricePerDay * daysBetween;
    } else {
      calculatedPrice += element.advertisement.priceList.pricePerDay * daysBetween;
    }

    return calculatedPrice;
  }

  calculateTotalPrice() {
    let totalPrice = 0.0;
    this.rentInfos.forEach(item => totalPrice += this.calculatePrice(item));

    return totalPrice;
  }

  viewDetails(element: RentInfo) {
    this.router.navigate(['/advertisement/' + element.advertisement.id]);
  }
}
