import { GearboxTypeService } from './../../../service/gearboxType.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { GearBoxType } from 'src/app/model/gearboxType';

@Component({
  selector: 'app-add-gear-box-type',
  templateUrl: './add-gear-box-type.component.html',
  styleUrls: ['./add-gear-box-type.component.css']
})
export class AddGearBoxTypeComponent implements OnInit {

  createForm: FormGroup;

  constructor(private toastr: ToastrService, private gearboxTypeServce: GearboxTypeService,
    private dialogRef: MatDialogRef<AddGearBoxTypeComponent>, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
  }

  create() {
    this.gearboxTypeServce.create(new GearBoxType(this.createForm.value.name)).subscribe(
      (data: GearBoxType) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully added a new GearBox Type.', 'Create GearBox Type');
        this.gearboxTypeServce.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create GearBox Type');
      }
    );
  }


}
