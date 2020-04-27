import { GearboxTypeService } from './../../../service/gearboxType.service';
import { BodyStyleService } from './../../../service/bodyStyle.service';
import { FuelTypeService } from './../../../service/fuelType.service';
import { BodyStyle } from './../../../model/bodystyle';
import { GearBoxType } from './../../../model/gearboxType';
import { FuelType } from './../../../model/fuelType';
import { CarEdit } from './../../../model/carEdit';
import { CarWithPictures } from './../../../model/carWithPictures';
import { CarService } from './../../../service/car.service';
import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Car } from 'src/app/model/car';

@Component({
  selector: 'app-edit-car',
  templateUrl: './edit-car.component.html',
  styleUrls: ['./edit-car.component.css']
})
export class EditCarComponent implements OnInit {

  editForm: FormGroup;
  files: File[] = [];
  fuelTypes: FuelType[] = [];
  gearBoxTypes: GearBoxType[] = [];
  bodyStyles: BodyStyle[] = [];

  constructor(private toastr: ToastrService, private carService: CarService, private fuelTypeService: FuelTypeService, private gearboxTypeService: GearboxTypeService,
    private bodyStyleService: BodyStyleService, private dialogRef: MatDialogRef<EditCarComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public selectedItem: CarWithPictures) { }

  ngOnInit() {
    this.fetchFuelTypes();
    this.fetchGearboxTypes();
    this.fetchBodyStyles();
    this.editForm = this.formBuilder.group({
      make: new FormControl(this.selectedItem.carDTO.make, Validators.required),
      model: new FormControl(this.selectedItem.carDTO.model, Validators.required),
      fuelType: new FormControl(null, Validators.required),
      gearBoxType: new FormControl(null, Validators.required),
      bodyStyle: new FormControl(null, Validators.required),
      mileageInKm: new FormControl(this.selectedItem.carDTO.mileageInKm, [Validators.required, Validators.min(0)]),
      kidsSeats: new FormControl(this.selectedItem.carDTO.kidsSeats, [Validators.required, Validators.min(0), Validators.max(3)]),
      availableTracking: new FormControl(this.selectedItem.carDTO.availableTracking, Validators.required),
    })

    this.fetchPictures();
  }

  selectBodyStyle() {
    this.bodyStyles.forEach((element: BodyStyle) => {
      if (element.id === this.selectedItem.carDTO.bodyStyle.id) {
        this.editForm.controls['bodyStyle'].setValue(element);
      }
    });
  }

  selectfetchFuelType() {
    this.fuelTypes.forEach((element: FuelType) => {
      if (element.id === this.selectedItem.carDTO.fuelType.id) {
        this.editForm.controls['fuelType'].setValue(element);
      }
    });
  }

  selectfetchGearboxType() {
    this.gearBoxTypes.forEach((element: GearBoxType) => {
      if (element.id === this.selectedItem.carDTO.gearBoxType.id) {
        this.editForm.controls['gearBoxType'].setValue(element);
      }
    });
  }

  fetchBodyStyles() {
    this.bodyStyleService.getBodyStyles().subscribe(
      (data: BodyStyle[]) => {
        this.bodyStyles = data;
        this.selectBodyStyle();
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
        this.selectfetchFuelType();
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
        this.selectfetchGearboxType();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.gearBoxTypes = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Gearbox Types');
      }
    );
  }

  edit() {
    if (this.editForm.invalid) {
      this.toastr.error("Please enter valid data", 'Create car');
      return;
    }
    if (this.files.length == 0 || this.files.length > 5) {
      this.toastr.error("You have to choose at least one picture.Max number of pictures is 5.", 'Create Car');
      return;
    }
    const formData = new FormData();
    const car = new Car(this.editForm.value.make, this.editForm.value.model, this.editForm.value.fuelType, this.editForm.value.gearBoxType, this.editForm.value.bodyStyle,
      this.editForm.value.mileageInKm, this.editForm.value.kidsSeats, this.editForm.value.availableTracking, this.selectedItem.carDTO.id);
    formData.append("carData", JSON.stringify(car));

    for (var i = 0; i < this.files.length; i++) {
      formData.append("files", this.files[i]);
    }

    this.carService.edit(formData, this.selectedItem.carDTO.id).subscribe(
      (data: Car) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully edited a Car.', 'Edit Car');
        this.carService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        console.log(httpErrorResponse)
        this.toastr.error(httpErrorResponse.error.message, 'Edit Car');
      }
    );
  }


  onSelect(event) {
    this.files.push(...event.addedFiles);
  }

  onRemove(event) {
    this.files.splice(this.files.indexOf(event), 1);
  }

  fetchPictures() {
    this.selectedItem.pictures.forEach(element => {
      this.carService.getPicture(element, this.selectedItem.carDTO.id).subscribe(
        (data: Blob) => {
          data.type
          const file = new File([data], element, { type: data.type });
          this.files.push(file);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, 'Get picture');
        }

      );
    });
  }

}
