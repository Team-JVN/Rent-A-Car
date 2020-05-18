import { GearBoxType } from './../../../model/gearboxType';
import { GearboxTypeService } from './../../../service/gearboxType.service';

import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-gear-box-type',
  templateUrl: './edit-gear-box-type.component.html',
  styleUrls: ['./edit-gear-box-type.component.css']
})
export class EditGearBoxTypeComponent implements OnInit {

  editForm: FormGroup;

  constructor(private toastr: ToastrService, private gearboxTypeService: GearboxTypeService,
    private dialogRef: MatDialogRef<EditGearBoxTypeComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public selectedItem: GearBoxType) { }

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
    this.gearboxTypeService.edit(new GearBoxType(this.editForm.value.name, this.selectedItem.id)).subscribe(
      (data: GearBoxType) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully edited a Gearbox Type.', 'Edit Gearbox Type');
        this.gearboxTypeService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Gearbox Type');
      }
    );
  }

}
