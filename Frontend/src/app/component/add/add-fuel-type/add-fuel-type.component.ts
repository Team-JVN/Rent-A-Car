import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef } from '@angular/material/dialog';
import { FuelTypeService } from './../../../service/fuelType.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { FuelType } from 'src/app/model/fuelType';

@Component({
  selector: 'app-add-fuel-type',
  templateUrl: './add-fuel-type.component.html',
  styleUrls: ['./add-fuel-type.component.css']
})
export class AddFuelTypeComponent implements OnInit {

  createForm: FormGroup;

  constructor(private toastr: ToastrService, private fuelTypeService: FuelTypeService,
    private dialogRef: MatDialogRef<AddFuelTypeComponent>, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
  }

  create() {
    this.fuelTypeService.create(new FuelType(this.createForm.value.name)).subscribe(
      (data: FuelType) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Create Fuel Type');
        this.fuelTypeService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Fuel Type');
      }
    );
  }

}
