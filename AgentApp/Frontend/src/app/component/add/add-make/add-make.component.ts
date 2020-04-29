import { HttpErrorResponse } from '@angular/common/http';
import { MakeService } from './../../../service/make.service';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Make } from 'src/app/model/make';

@Component({
  selector: 'app-add-make',
  templateUrl: './add-make.component.html',
  styleUrls: ['./add-make.component.css']
})
export class AddMakeComponent implements OnInit {

  createForm: FormGroup;

  constructor(private toastr: ToastrService, private makeService: MakeService,
    private dialogRef: MatDialogRef<AddMakeComponent>, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required)
    })
  }

  create() {
    this.makeService.create(new Make(this.createForm.value.name)).subscribe(
      (data: Make) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Create Make');
        this.makeService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Make');
      }
    );
  }

}
