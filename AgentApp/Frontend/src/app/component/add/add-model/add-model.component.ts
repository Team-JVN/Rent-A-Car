import { HttpErrorResponse } from '@angular/common/http';
import { MakeService } from './../../../service/make.service';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit, Inject } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Make } from 'src/app/model/make';
import { Model } from 'src/app/model/model';

@Component({
  selector: 'app-add-model',
  templateUrl: './add-model.component.html',
  styleUrls: ['./add-model.component.css']
})
export class AddModelComponent implements OnInit {

  createForm: FormGroup;

  constructor(private toastr: ToastrService, private makeService: MakeService,
    private dialogRef: MatDialogRef<AddModelComponent>, private formBuilder: FormBuilder, @Inject(MAT_DIALOG_DATA) public data: number) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
  }

  create() {
    this.makeService.createModel(this.data, new Model(this.createForm.value.name)).subscribe(
      (data: Model) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Create Model');
        this.makeService.createModelSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Model');
      }
    );
  }
}
