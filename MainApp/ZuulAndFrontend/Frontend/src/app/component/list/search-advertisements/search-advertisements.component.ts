import { AdvertisementFromSearch } from './../../../model/advertisementFromSearch';
import { SearchParams } from './../../../model/searchParams';
import { SearchService } from './../../../service/search.service';
import { MakeService } from './../../../service/make.service';
import { BodyStyleService } from './../../../service/bodyStyle.service';
import { GearboxTypeService } from './../../../service/gearboxType.service';
import { FuelTypeService } from './../../../service/fuelType.service';
import { Make } from './../../../model/make';
import { Model } from './../../../model/model';
import { BodyStyle } from '../../../model/bodyStyle';
import { GearBoxType } from './../../../model/gearboxType';
import { FuelType } from './../../../model/fuelType';
import { FormGroup, ValidatorFn, FormControl, FormBuilder, Validators } from '@angular/forms';
import { AdvertisementWithPictures } from './../../../model/advertisementWithPictures';
import { RentRequestService } from './../../../service/rent-request.service';
import { AddRentRequestComponent } from './../../add/add-rent-request/add-rent-request.component';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../../service/car.service';
import { AdvertisementService } from './../../../service/advertisement.service';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { StarRatingComponent } from 'ng-starrating';
import { formatDate } from '@angular/common';

const DateValidator: ValidatorFn = (fg: FormGroup) => {
  const from = fg.get('dateFrom').value;
  const to = fg.get('dateTo').value;
  if (!from || !to) {
    return null;
  }
  return from !== null && to !== null && from < to ? null : { validError: true };
};

@Component({
  selector: 'app-search-advertisements',
  templateUrl: './search-advertisements.component.html',
  styleUrls: ['./search-advertisements.component.css']
})
export class SearchAdvertisementsComponent implements OnInit {

  displayedColumns: string[] = ['advertisement'];
  advertisementsDataSource: MatTableDataSource<AdvertisementFromSearch>;
  searchForm: FormGroup;
  fuelTypes: FuelType[] = [];
  gearBoxTypes: GearBoxType[] = [];
  bodyStyles: BodyStyle[] = [];
  makes: Make[] = [];
  models: Model[] = [];
  status: string = 'all';
  minRating: number = 0.0;
  minDate: Date;

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private formBuilder: FormBuilder,
    private advertisementService: AdvertisementService,
    private searchService: SearchService,
    private carService: CarService,
    private toastr: ToastrService,
    private fuelTypeService: FuelTypeService,
    private gearboxTypeService: GearboxTypeService,
    private bodyStyleService: BodyStyleService,
    private makeService: MakeService,
    private rentRequestService: RentRequestService
  ) { }

  ngOnInit() {
    this.minDate = new Date();
    this.minDate.setDate(this.minDate.getDate() + 2);

    this.searchForm = this.formBuilder.group({
      dateFrom: new FormControl(null, Validators.required),
      timeFrom: new FormControl(null, Validators.required),
      dateTo: new FormControl(null, Validators.required),
      timeTo: new FormControl(null, Validators.required),
      pickUpPoint: new FormControl(null, Validators.required),
      make: new FormControl(),
      model: new FormControl(),
      fuelType: new FormControl(),
      gearBoxType: new FormControl(),
      bodyStyle: new FormControl(),
      minPricePerDay: new FormControl(),
      maxPricePerDay: new FormControl(),
      kidsSeats: new FormControl(null, [Validators.min(0), Validators.max(3)]),
      mileageInKm: new FormControl(null, Validators.min(0)),
      kilometresLimit: new FormControl(null, Validators.min(0)),
      cdw: new FormControl(null),
    }, {
      validator: [DateValidator]
    })
    this.fetchAll();

    this.fetchMakes();
    this.fetchFuelTypes();
    this.fetchGearboxTypes();
    this.fetchBodyStyles();
  }

  //TODO: change from AdvertisementWithPictures to AdvertisementFromSearch
  rent(element: AdvertisementWithPictures) {
    this.dialog.open(AddRentRequestComponent, { data: element });
  }

  //TODO: change from AdvertisementWithPictures to AdvertisementFromSearch
  viewDetails(element: AdvertisementWithPictures) {
    this.router.navigate(['/advertisement/' + element.id]);
  }

  onRate($event: { oldValue: number, newValue: number, starRating: StarRatingComponent }) {
    this.minRating = $event.newValue;
  }

  clearSearch() {
    this.searchForm.reset();
    this.minRating = 0.0;
  }

  search() {
    if (this.searchForm.invalid) {
      this.toastr.error("Please enter valid dates and times", 'Search Advertisements');
      return;
    }

    const dateFrom = formatDate(this.searchForm.value.dateFrom, 'yyyy-MM-dd', 'en-US')
    const dateTimeFrom = dateFrom + ' ' + this.searchForm.value.timeFrom;
    const dateTo = formatDate(this.searchForm.value.dateTo, 'yyyy-MM-dd', 'en-US')
    const dateTimeTo = dateTo + ' ' + this.searchForm.value.timeTo;

    const searchParams = new SearchParams(dateTimeFrom, dateTimeTo, this.searchForm.value.pickUpPoint, this.searchForm.value.make.name,
      this.searchForm.value.model.name, this.searchForm.value.fuelType.name, this.searchForm.value.gearBoxType.name,
      this.searchForm.value.bodyStyle.name, this.minRating, this.searchForm.value.minPricePerDay, this.searchForm.value.maxPricePerDay,
      this.searchForm.value.kidsSeats, this.searchForm.value.mileageInKm, this.searchForm.value.kilometresLimit, this.searchForm.value.cdw);

    this.searchService.searchAdvertisements(searchParams).subscribe(
      (data: AdvertisementFromSearch[]) => {
        data.forEach(adFromSearch => {
          this.getPicture(adFromSearch);
        });
        this.advertisementsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Search Advertisements');
      }
    );
  }

  //TODO: change from AdvertisementWithPictures to AdvertisementFromSearch
  checkIfCanRentAdvertisement(element: AdvertisementWithPictures): boolean {
    if (!element.dateTo) {
      return true;
    }
    if (new Date(element.dateTo) > new Date()) {
      return true;
    }
    return false;
  }

  fetchAll() {
    this.searchService.getAll().subscribe(
      (data: AdvertisementFromSearch[]) => {
        data.forEach(adFromSearch => {
          this.getPicture(adFromSearch);
        });
        this.advertisementsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: AdvertisementFromSearch[] = []
        this.advertisementsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Advertisements');
      }
    );
  }

  fetchBodyStyles() {
    this.bodyStyleService.getBodyStyles().subscribe(
      (data: BodyStyle[]) => {
        this.bodyStyles = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.bodyStyles = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Body Styles');
      }
    );
  }

  fetchFuelTypes(): void {
    this.fuelTypeService.getFuelTypes().subscribe(
      (data: FuelType[]) => {
        this.fuelTypes = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.fuelTypes = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Fuel Types');
      }
    );
  }

  fetchGearboxTypes() {
    this.gearboxTypeService.getGearboxTypes().subscribe(
      (data: GearBoxType[]) => {
        this.gearBoxTypes = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.gearBoxTypes = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Gearbox Types');
      }
    );
  }

  fetchMakes() {
    this.makeService.getMakes().subscribe(
      (data: Make[]) => {
        this.makes = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.makes = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Makes');
      }
    );
  }

  fetchModels() {
    this.makeService.getModels(this.searchForm.value.make.id).subscribe(
      (data: Model[]) => {
        this.models = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.models = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Models');
      }
    );
  }

  getPicture(adFromSearch: AdvertisementFromSearch) {
    this.carService.getPicture(adFromSearch.car.pictures[0], adFromSearch.car.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, adFromSearch);
        adFromSearch.car.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
      }
    );
  }

  createImageFromBlob(image: Blob, adFromSearch: AdvertisementFromSearch) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      adFromSearch.car.image = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

}
