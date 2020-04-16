import { Car } from './../../../model/car';
import { BodyStyle } from 'src/app/model/bodystyle';
import { BodyStyleService } from './../../../service/bodyStyle.service';
import { GearboxTypeService } from './../../../service/gearboxType.service';
import { HttpErrorResponse } from '@angular/common/http';
import { GearBoxType } from './../../../model/gearboxType';
import { FuelType } from './../../../model/fuelType';
import { FuelTypeService } from './../../../service/fuelType.service';
import { MatDialogRef } from '@angular/material/dialog';
import { CarService } from './../../../service/car.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-add-car',
  templateUrl: './add-car.component.html',
  styleUrls: ['./add-car.component.css']
})
export class AddCarComponent implements OnInit {
  carForm: FormGroup;
  fuelTypes: FuelType[] = [];
  gearBoxTypes: GearBoxType[] = [];
  bodyStyles: BodyStyle[] = [];
  files: File[] = [];
  constructor(private toastr: ToastrService, private carService: CarService, private fuelTypeService: FuelTypeService, private gearboxTypeService: GearboxTypeService,
    private bodyStyleService: BodyStyleService, private dialogRef: MatDialogRef<AddCarComponent>, private formBuilder: FormBuilder, ) { }

  ngOnInit() {
    this.carForm = this.formBuilder.group({
      make: new FormControl(null, Validators.required),
      model: new FormControl(null, Validators.required),
      fuelType: new FormControl(null, Validators.required),
      gearBoxType: new FormControl(null, Validators.required),
      bodyStyle: new FormControl(null, Validators.required),
      mileageInKm: new FormControl(null, [Validators.required, Validators.min(0)]),
      kidsSeats: new FormControl(null, [Validators.required, Validators.min(0), Validators.max(3)]),
      availableTracking: new FormControl(false, Validators.required),
    })
    this.fetchFuelTypes();
    this.fetchGearboxTypes();
    this.fetchBodyStyles();
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

  create() {
    if (this.carForm.invalid) {
      this.toastr.error("Please enter valid data", 'Create car');
      return;
    }
    if (this.files.length == 0 || this.files.length > 5) {
      this.toastr.error("You have to choose at least one picture.Max number of pictures is 5.", 'Create Car');
      return;
    }
    const formData = new FormData();
    const car = new Car(this.carForm.value.make, this.carForm.value.model, this.carForm.value.fuelType, this.carForm.value.gearBoxType, this.carForm.value.bodyStyle,
      this.carForm.value.mileageInKm, this.carForm.value.kidsSeats, this.carForm.value.availableTracking);
    formData.append("carData", JSON.stringify(car));

    for (var i = 0; i < this.files.length; i++) {
      formData.append("files", this.files[i]);
    }
    this.carService.create(formData).subscribe(
      (data: Car) => {
        this.carForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully added a new car.', 'Create Car');
        this.carService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Car');
      }
    );
  }

  onSelect(event) {
    this.files.push(...event.addedFiles);
  }

  onRemove(event) {
    this.files.splice(this.files.indexOf(event), 1);
  }
}
