import { Model } from './../../../model/model';
import { Make } from './../../../model/make';
import { MakeService } from './../../../service/make.service';
import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-edit-model',
  templateUrl: './edit-model.component.html',
  styleUrls: ['./edit-model.component.css']
})
export class EditModelComponent implements OnInit {
  editForm: FormGroup;

  constructor(private toastr: ToastrService, private makeService: MakeService,
    private dialogRef: MatDialogRef<EditModelComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
    this.editForm.patchValue(
      {
        'name': this.data.model.name
      }
    );
  }

  edit() {
    this.makeService.editModel(this.data.makeId, new Model(this.editForm.value.name, this.data.model.id)).subscribe(
      (data: Make) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully edited a Model.', 'Edit Model');
        this.makeService.createModelSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Model');
      }
    );
  }
}
