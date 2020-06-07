import { CarEdit } from '../../../model/carEdit';
import { CarWithPictures } from './../../../model/carWithPictures';
import { CarService } from './../../../service/car.service';
import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Car } from 'src/app/model/car';

@Component({
  selector: 'app-edit-car-partial',
  templateUrl: './edit-car-partial.component.html',
  styleUrls: ['./edit-car-partial.component.css']
})
export class EditCarPartialComponent implements OnInit {
  editForm: FormGroup;
  files: File[] = [];

  constructor(private toastr: ToastrService, private carService: CarService,
    private dialogRef: MatDialogRef<EditCarPartialComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public selectedItem: CarWithPictures) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      mileageInKm: new FormControl(this.selectedItem.mileageInKm, [Validators.required, Validators.min(0)]),
      kidsSeats: new FormControl(this.selectedItem.kidsSeats, [Validators.required, Validators.min(0), Validators.max(3)]),
      availableTracking: new FormControl(this.selectedItem.availableTracking, Validators.required),
    })

    this.fetchPictures();
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
    const car = new CarEdit(this.editForm.value.mileageInKm, this.editForm.value.kidsSeats, this.editForm.value.availableTracking, this.selectedItem.id);
    formData.append("carData", JSON.stringify(car));

    for (var i = 0; i < this.files.length; i++) {
      formData.append("files", this.files[i]);
    }

    this.carService.editPartial(formData, this.selectedItem.id).subscribe(
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
