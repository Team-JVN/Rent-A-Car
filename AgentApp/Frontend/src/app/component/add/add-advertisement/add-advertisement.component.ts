import { CarWithPicturesDTO } from './../../../model/carWithPictures';
import { Subscription } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { Car } from './../../../model/car';
import { BodyStyle } from 'src/app/model/bodystyle';
import { BodyStyleService } from './../../../service/bodyStyle.service';
import { GearboxTypeService } from './../../../service/gearboxType.service';
import { HttpErrorResponse } from '@angular/common/http';
import { GearBoxType } from './../../../model/gearboxType';
import { FuelType } from './../../../model/fuelType';
import { FuelTypeService } from './../../../service/fuelType.service';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { CarService } from './../../../service/car.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { AddCarComponent } from '../add-car/add-car.component';

@Component({
  selector: 'app-add-advertisement',
  templateUrl: './add-advertisement.component.html',
  styleUrls: ['./add-advertisement.component.css']
})
export class AddAdvertisementComponent implements OnInit {
  carForm: FormGroup;
  cars: CarWithPicturesDTO[] = [];
  successCreated: Subscription;
  // gearBoxTypes: GearBoxType[] = [];
  // bodyStyles: BodyStyle[] = [];
  constructor(private toastr: ToastrService, private carService: CarService,
    private dialogRef: MatDialogRef<AddAdvertisementComponent>, private formBuilder: FormBuilder, public dialog: MatDialog) { }

  ngOnInit() {
    this.carForm = this.formBuilder.group({
      car: new FormControl(null, Validators.required),
    })
    // this.carForm = this.formBuilder.group({
    //   make: new FormControl(null, Validators.required),
    //   model: new FormControl(null, Validators.required),
    //   fuelType: new FormControl(null, Validators.required),
    //   gearBoxType: new FormControl(null, Validators.required),
    //   bodyStyle: new FormControl(null, Validators.required),
    //   mileageInKm: new FormControl(null, [Validators.required, Validators.min(0)]),
    //   kidsSeats: new FormControl(null, [Validators.required, Validators.min(0), Validators.max(3)]),
    //   availableTracking: new FormControl(false, Validators.required),
    // })

    this.successCreated = this.carService.createSuccessEmitter.subscribe(
      () => {
        this.fetchCars()
      }
    );

    this.fetchCars();
    // this.fetchGearboxTypes();
    // this.fetchBodyStyles();
  }

  fetchCars() {
    this.carService.getCars().subscribe(
      (data: CarWithPicturesDTO[]) => {
        this.cars = data;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.cars = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );
  }

  getSelectedCar() {
    return this.carForm.get('car').value;
  }

  create() {
    // if (this.carForm.invalid) {
    //   this.toastr.error("Please enter valid data", 'Create car');
    //   return;
    // }
    // if (this.files.length == 0 || this.files.length > 5) {
    //   this.toastr.error("You have to choose at least one picture.Max number of pictures is 5.", 'Create Car');
    //   return;
    // }
    // const formData = new FormData();
    // const car = new Car(this.carForm.value.make, this.carForm.value.model, this.carForm.value.fuelType, this.carForm.value.gearBoxType, this.carForm.value.bodyStyle,
    //   this.carForm.value.mileageInKm, this.carForm.value.kidsSeats, this.carForm.value.availableTracking);
    // formData.append("carData", JSON.stringify(car));

    // for (var i = 0; i < this.files.length; i++) {
    //   formData.append("files", this.files[i]);
    // }
    // this.carService.create(formData).subscribe(
    //   (data: Car) => {
    //     this.carForm.reset();
    //     this.dialogRef.close();
    //     this.toastr.success('Successfully added a new car.', 'Create Car');
    //     this.carService.createSuccessEmitter.next(data);
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     this.toastr.error(httpErrorResponse.error.message, 'Create Car');
    //   }
    // );
  }

  openAddCar() {
    this.dialog.open(AddCarComponent);
  }

}
