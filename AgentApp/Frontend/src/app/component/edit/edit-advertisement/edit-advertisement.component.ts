import { AddPriceListComponent } from './../../add/add-price-list/add-price-list.component';
import { AddCarComponent } from './../../add/add-car/add-car.component';
import { PriceList } from './../../../model/priceList';
import { Subscription } from 'rxjs';
import { CarService } from './../../../service/car.service';
import { AdvertisementWithPictures } from './../../../model/advertisementWithPictures';
import { AdvertisementService } from './../../../service/advertisement.service';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { BodyStyle } from 'src/app/model/bodyStyle';
import { CarWithPictures } from 'src/app/model/carWithPictures';
import { PriceListService } from 'src/app/service/price-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { formatDate } from '@angular/common';
import { Advertisement } from 'src/app/model/advertisement';
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
    private dialogRef: MatDialogRef<EditAdvertisementComponent>, @Inject(MAT_DIALOG_DATA) public selectedItem: AdvertisementWithPictures,
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

  selectPriceList() {
    this.priceLists.forEach((element: PriceList) => {
      if (element.id === this.selectedItem.priceList.id) {
        this.priceListForm.controls['priceList'].setValue(element);
      }
    });
  }

  selectCar() {
    this.cars.forEach((element: CarWithPictures) => {
      if (element.carDTO.id === this.selectedItem.carWithPicturesDTO.carDTO.id) {
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

    const advertisement = new Advertisement(this.carForm.value.car.carDTO, this.priceListForm.value.priceList,
      this.dateForm.value.discount, this.dateForm.value.kilometresLimit, validFrom, this.selectedItem.id);

    this.advertisementService.edit(advertisement).subscribe(
      (data: Advertisement) => {
        this.carForm.reset();
        this.priceListForm.reset();
        this.toastr.success('Success.', 'Edit Advertisement');
        this.advertisementService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse)
        this.toastr.error(httpErrorResponse.error.message, 'Edit Advertisement');
      }
    );
  }

}
