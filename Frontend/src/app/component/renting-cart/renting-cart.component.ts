import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { UserInfo } from './../../model/userInfo';
import { Router } from '@angular/router';
import { CarService } from './../../service/car.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CarWithPictures } from './../../model/carWithPictures';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { RentInfo } from 'src/app/model/rentInfo';
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
  owners: UserInfo[] = [];
  owner: UserInfo;
  allOwners: UserInfo;

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private toastr: ToastrService,
    private carService: CarService,
  ) { }

  ngOnInit() {
    this.allOwners = new UserInfo('- All -', '- All -');
    this.owner = this.allOwners;
    this.fetchRentInfos();
  }

  fetchRentInfos() {
    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    rentInfos.forEach(rentInfo => {
      this.getPicture(rentInfo.advertisement.car)
    })

    this.owners = Object.values(rentInfos.reduce(
      function (map, item) {
        map[item.advertisement.car.owner.email] = item.advertisement.car.owner;
        return map;
      }, {}));
    this.rentInfosDataSource = new MatTableDataSource(rentInfos);
  }

  fetchFromOwner() {
    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    if (this.owner !== this.allOwners) {
      rentInfos = rentInfos.filter(item => item.advertisement.car.owner.email === this.owner.email);
    }
    this.rentInfosDataSource = new MatTableDataSource(rentInfos);
  }

  clickedOnCheckbox(element: RentInfo) {

  }

  calculatePrice(element: RentInfo) {
    const dateTimeFrom = moment(element.dateTimeFrom);
    const dateTimeTo = moment(element.dateTimeTo);
    const daysBetween = Math.ceil(dateTimeTo.diff(dateTimeFrom, 'days', true));

    let calculatedPrice = 0;
    if (element.optedForCDW) {
      calculatedPrice += element.advertisement.priceList.priceForCDW * daysBetween;
    }
    if (daysBetween > 30) {
      const reducedPricePerDay = element.advertisement.priceList.pricePerDay * (1.0 - (element.advertisement.discount / 100.0));
      calculatedPrice += reducedPricePerDay * daysBetween;
    } else {
      calculatedPrice += element.advertisement.priceList.pricePerDay * daysBetween;
    }

    return calculatedPrice;
  }

  emptyCart() {
    localStorage.removeItem("rentInfos");
    this.fetchRentInfos();
  }

  removeItem(index: number) {
    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    if (index != -1) {
      rentInfos.splice(index, 1);
    }
    localStorage.setItem("rentInfos", JSON.stringify(rentInfos));
    this.fetchRentInfos();
  }

  createRentRequest() {

  }

  viewDetails(element: RentInfo) {
    this.router.navigate(['/advertisement/' + element.advertisement.id]);
  }

  getPicture(carWithPicturesDTO: CarWithPictures) {
    this.carService.getPicture(carWithPicturesDTO.pictures[0].data, carWithPicturesDTO.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, carWithPicturesDTO);
        carWithPicturesDTO.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
      }

    );
  }

  createImageFromBlob(image: Blob, carWithPicturesDTO: CarWithPictures) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      carWithPicturesDTO.image = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

}
