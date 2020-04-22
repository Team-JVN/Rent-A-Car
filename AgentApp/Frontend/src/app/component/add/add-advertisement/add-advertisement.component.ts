import { Advertisement } from './../../../model/advertisement';
import { AddPriceListComponent } from './../add-price-list/add-price-list.component';
import { PriceList } from './../../../model/priceList';
import { PriceListService } from './../../../service/price-list.service';
import { CarWithPictures } from './../../../model/carWithPictures';
import { Subscription } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { CarService } from './../../../service/car.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { AddCarComponent } from '../add-car/add-car.component';
import { AdvertisementService } from 'src/app/service/advertisement.service';
import { formatDate, DatePipe } from '@angular/common';

@Component({
  selector: 'app-add-advertisement',
  templateUrl: './add-advertisement.component.html',
  styleUrls: ['./add-advertisement.component.css']
})
export class AddAdvertisementComponent implements OnInit {
  carForm: FormGroup;
  priceListForm: FormGroup;
  dateForm: FormGroup;
  minDate = new Date();

  cars: CarWithPictures[] = [];
  priceLists: PriceList[] = [];
  successCreatedCar: Subscription;
  successCreatedList: Subscription;

  constructor(private toastr: ToastrService, private carService: CarService, private priceListService: PriceListService, private advertisementService: AdvertisementService,
    private dialogRef: MatDialogRef<AddAdvertisementComponent>, private formBuilder: FormBuilder, public dialog: MatDialog) { }

  ngOnInit() {
    this.carForm = this.formBuilder.group({
      car: new FormControl(null, Validators.required),
    })

    this.priceListForm = this.formBuilder.group({
      priceList: new FormControl(null, Validators.required)
    })

    this.dateForm = this.formBuilder.group({
      validFrom: new FormControl(null, Validators.required),
      discount: new FormControl(null, [Validators.min(0), Validators.max(99)]),
      kilometresLimit: new FormControl(null, Validators.min(1))
    })

    this.successCreatedCar = this.carService.createSuccessEmitter.subscribe(
      () => {
        this.fetchCars()
      }
    );

    this.successCreatedList = this.priceListService.createSuccessEmitter.subscribe(
      () => {
        this.fetchPriceLists();
      }
    )

    this.fetchCars();
    this.fetchPriceLists();
  }

  fetchCars() {
    this.carService.getCars().subscribe(
      (data: CarWithPictures[]) => {
        this.cars = data;
        this.carForm.patchValue(
          {
            'car': null
          }
        );
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.cars = [];
        this.toastr.error(httpErrorResponse.error.message, 'Display Cars');
      }
    );
  }

  fetchPriceLists() {
    this.priceListService.getAll().subscribe(
      (data: PriceList[]) => {
        this.priceLists = data;
        this.priceListForm.patchValue(
          {
            'priceList': null
          }
        );
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: PriceList[] = []
        this.priceLists = data;
        this.toastr.error(httpErrorResponse.error.message, 'Display Price Lists');
      }
    )
  }

  getSelectedCar() {
    return this.carForm.get('car').value;
  }

  getSelectedPriceList() {
    return this.priceListForm.get('priceList').value;
  }

  create() {
    if (this.getSelectedPriceList().pricePerKm && !this.dateForm.value.kilometresLimit) {
      this.toastr.error("Please enter Kilometres limit", 'Create Advertisement');
      return;
    }
    const validFrom = formatDate(this.dateForm.value.validFrom, 'yyyy-MM-dd', 'en-US')
    var cdw = true;
    if (!this.priceListForm.value.priceList.priceForCDW) {
      cdw = false;
    }
    console.log(this.dateForm.value.validFrom)
    const advertisement = new Advertisement(this.carForm.value.car.carDTO, this.priceListForm.value.priceList,
      this.dateForm.value.discount, this.dateForm.value.kilometresLimit, cdw, validFrom);

    this.advertisementService.create(advertisement).subscribe(
      (data: Advertisement) => {
        this.carForm.reset();
        this.priceListForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success.', 'Create Advertisement');
        this.advertisementService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Advertisement');
      }
    );
  }

  openAddCar() {
    this.dialog.open(AddCarComponent);
  }

  openAddPriceList() {
    this.dialog.open(AddPriceListComponent);
  }
}
