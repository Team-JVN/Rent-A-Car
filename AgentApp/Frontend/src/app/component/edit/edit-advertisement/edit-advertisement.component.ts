import { DateTime } from 'luxon';
import { AdvertisementWithPicturesDTO } from './../../../model/advertisementWithPictures';
import { AddPriceListComponent } from './../../add/add-price-list/add-price-list.component';
import { AddCarComponent } from './../../add/add-car/add-car.component';
import { PriceList } from './../../../model/priceList';
import { Subscription } from 'rxjs';
import { CarService } from './../../../service/car.service';
import { AdvertisementService } from './../../../service/advertisement.service';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { CarWithPictures } from 'src/app/model/carWithPictures';
import { PriceListService } from 'src/app/service/price-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatDate, DatePipe } from '@angular/common';
import { Advertisement } from 'src/app/model/advertisement';
import { parse } from 'querystring';
import * as moment from 'moment';
@Component({
  selector: 'app-edit-advertisement',
  templateUrl: './edit-advertisement.component.html',
  styleUrls: ['./edit-advertisement.component.css']
})
export class EditAdvertisementComponent implements OnInit {
  carForm: FormGroup;
  priceListForm: FormGroup;
  dateForm: FormGroup;
  minDate = new Date();
  cars: CarWithPictures[] = [];
  priceLists: PriceList[] = [];
  successCreatedCar: Subscription;
  successCreatedList: Subscription;

  constructor(private toastr: ToastrService, private carService: CarService, private priceListService: PriceListService, private advertisementService: AdvertisementService,
    private dialogRef: MatDialogRef<EditAdvertisementComponent>, @Inject(MAT_DIALOG_DATA) public selectedItem: AdvertisementWithPicturesDTO,
    private formBuilder: FormBuilder, public dialog: MatDialog) { }

  ngOnInit() {
    this.carForm = this.formBuilder.group({
      car: new FormControl(null, Validators.required),
    })

    this.priceListForm = this.formBuilder.group({
      priceList: new FormControl(null, Validators.required)
    })

    this.dateForm = this.formBuilder.group({
      validFrom: new FormControl(null, Validators.required),
      discount: new FormControl(this.selectedItem.advertisementDTO.discount, [Validators.min(0), Validators.max(99)]),
      kilometresLimit: new FormControl(this.selectedItem.advertisementDTO.kilometresLimit, Validators.min(1))
    })
    // const validFrom = formatDate(this.selectedItem.advertisementDTO.dateFrom, 'yyyy-MM-dd', 'en-US')
    console.log(moment(this.selectedItem.advertisementDTO.dateFrom, 'dd-MM-yyyy'))
    console.log(this.selectedItem.advertisementDTO.dateFrom)
    // this.dateForm.patchValue(
    //   {
    //     'validFrom': new Date(moment(this.selectedItem.advertisementDTO.dateFrom, 'dd-MM-yyyy'))
    //   }
    // );

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
        this.selectCar();
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
        this.selectPriceList();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: PriceList[] = []
        this.priceLists = data;
        this.toastr.error(httpErrorResponse.error.message, 'Display Price Lists');
      }
    )
  }

  selectPriceList() {
    this.priceLists.forEach((element: PriceList) => {
      if (element.id === this.selectedItem.advertisementDTO.priceList.id) {
        this.priceListForm.controls['priceList'].setValue(element);
      }
    });
  }

  selectCar() {
    this.cars.forEach((element: CarWithPictures) => {
      if (element.carDTO.id === this.selectedItem.advertisementDTO.car.id) {
        this.carForm.controls['car'].setValue(element);
      }
    });
  }

  getSelectedCar() {
    return this.carForm.get('car').value;
  }

  getSelectedPriceList() {
    return this.priceListForm.get('priceList').value;
  }

  openAddCar() {
    this.dialog.open(AddCarComponent);
  }

  openAddPriceList() {
    this.dialog.open(AddPriceListComponent);
  }

  edit() {
    if (this.getSelectedPriceList().pricePerKm && !this.dateForm.value.kilometresLimit) {
      this.toastr.error("Please enter Kilometres limit", 'Create Advertisement');
      return;
    }

    const validFrom = formatDate(this.dateForm.value.validFrom, 'dd-MM-yyyy', 'en-US')
    var cdw = true;
    if (!this.priceListForm.value.priceList.priceForCDW) {
      cdw = false;
    }
    const advertisement = new Advertisement(this.carForm.value.car.carDTO, this.priceListForm.value.priceList,
      this.dateForm.value.discount, this.dateForm.value.kilometresLimit, cdw, validFrom, this.selectedItem.advertisementDTO.id);

    this.advertisementService.edit(advertisement).subscribe(
      (data: Advertisement) => {
        this.carForm.reset();
        this.priceListForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success.', 'Edit Advertisement');
        this.advertisementService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Advertisement');
      }
    );
  }

}
