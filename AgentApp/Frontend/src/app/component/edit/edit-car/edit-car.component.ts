import { MakeService } from './../../../service/make.service';
import { Model } from 'src/app/model/model';
import { Make } from './../../../model/make';
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
  makes: Make[] = [];
  models: Model[] = [];

  constructor(private toastr: ToastrService, private carService: CarService, private fuelTypeService: FuelTypeService, private gearboxTypeService: GearboxTypeService,
    private bodyStyleService: BodyStyleService, private dialogRef: MatDialogRef<EditCarComponent>, private formBuilder: FormBuilder, private makeService: MakeService,
    @Inject(MAT_DIALOG_DATA) public selectedItem: CarWithPictures) { }

  ngOnInit() {

    this.editForm = this.formBuilder.group({
      make: new FormControl(this.selectedItem.make, Validators.required),
      model: new FormControl(this.selectedItem.model, Validators.required),
      fuelType: new FormControl(null, Validators.required),
      gearBoxType: new FormControl(null, Validators.required),
      bodyStyle: new FormControl(null, Validators.required),
      mileageInKm: new FormControl(this.selectedItem.mileageInKm, [Validators.required, Validators.min(0)]),
      kidsSeats: new FormControl(this.selectedItem.kidsSeats, [Validators.required, Validators.min(0), Validators.max(3)]),
      availableTracking: new FormControl(this.selectedItem.availableTracking, Validators.required),
    })

    this.fetchFuelTypes();
    this.fetchGearboxTypes();
    this.fetchBodyStyles();
    this.fetchMakes();
    this.fetchModels();
    this.fetchPictures();
  }

  selectBodyStyle() {
    this.bodyStyles.forEach((element: BodyStyle) => {
      if (element.id === this.selectedItem.bodyStyle.id) {
        this.editForm.controls['bodyStyle'].setValue(element);
      }
    });
  }

  selectfetchFuelType() {
    this.fuelTypes.forEach((element: FuelType) => {
      if (element.id === this.selectedItem.fuelType.id) {
        this.editForm.controls['fuelType'].setValue(element);
      }
    });
  }

  selectfetchGearboxType() {
    this.gearBoxTypes.forEach((element: GearBoxType) => {
      if (element.id === this.selectedItem.gearBoxType.id) {
        this.editForm.controls['gearBoxType'].setValue(element);
      }
    });
  }

  selectMake() {
    this.makes.forEach((element: Make) => {
      if (element.id === this.selectedItem.make.id) {
        this.editForm.controls['make'].setValue(element);
      }
    });
  }

  selectModel() {
    this.models.forEach((element: Model) => {
      if (element.id === this.selectedItem.model.id) {
        this.editForm.controls['model'].setValue(element);
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

  fetchMakes() {
    this.makeService.getMakes().subscribe(
      (data: Make[]) => {
        this.makes = data;
        this.selectMake();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.makes = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Makes');
      }
    );
  }

  fetchModels() {
    this.makeService.getModels(this.editForm.value.make.id).subscribe(
      (data: Model[]) => {
        this.models = data;
        this.selectModel();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.models = [];
        this.toastr.error(httpErrorResponse.error.message, 'Show Models');
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
      this.editForm.value.mileageInKm, this.editForm.value.kidsSeats, this.editForm.value.availableTracking, this.selectedItem.id);
    formData.append("carData", JSON.stringify(car));

    for (var i = 0; i < this.files.length; i++) {
      formData.append("files", this.files[i]);
    }

    this.carService.edit(formData, this.selectedItem.id).subscribe(
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
      this.carService.getPicture(element.data, this.selectedItem.id).subscribe(
        (data: Blob) => {
          data.type
          const file = new File([data], element.data, { type: data.type });
          this.files.push(file);
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, 'Get picture');
        }

      );
    });
  }

}
