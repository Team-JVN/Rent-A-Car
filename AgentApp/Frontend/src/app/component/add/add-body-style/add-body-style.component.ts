import { BodyStyleService } from './../../../service/bodyStyle.service';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { BodyStyle } from 'src/app/model/bodystyle';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-add-body-style',
  templateUrl: './add-body-style.component.html',
  styleUrls: ['./add-body-style.component.css']
})
export class AddBodyStyleComponent implements OnInit {

  createForm: FormGroup;

  constructor(private toastr: ToastrService, private bodyStyleService: BodyStyleService,
    private dialogRef: MatDialogRef<AddBodyStyleComponent>, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
  }

  create() {
    this.bodyStyleService.create(new BodyStyle(this.createForm.value.name)).subscribe(
      (data: BodyStyle) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully added a new Body Style.', 'Create Body Style');
        this.bodyStyleService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Body Style');
      }
    );
  }

}
