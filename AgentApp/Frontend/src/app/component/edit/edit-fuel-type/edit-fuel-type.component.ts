import { FuelType } from './../../../model/fuelType';
import { FuelTypeService } from './../../../service/fuelType.service';
import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-fuel-type',
  templateUrl: './edit-fuel-type.component.html',
  styleUrls: ['./edit-fuel-type.component.css']
})
export class EditFuelTypeComponent implements OnInit {

  editForm: FormGroup;

  constructor(private toastr: ToastrService, private fuelTypeService: FuelTypeService,
    private dialogRef: MatDialogRef<EditFuelTypeComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public selectedItem: FuelType) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
    this.editForm.patchValue(
      {
        'name': this.selectedItem.name
      }
    );
  }

  edit() {
    this.fuelTypeService.edit(new FuelType(this.editForm.value.name, this.selectedItem.id)).subscribe(
      (data: FuelType) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully edited a Fuel Type.', 'Edit Fuel Type');
        this.fuelTypeService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Fuel Type');
      }
    );
  }

}
