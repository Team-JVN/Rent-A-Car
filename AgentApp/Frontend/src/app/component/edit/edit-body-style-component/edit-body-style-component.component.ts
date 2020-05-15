import { HttpErrorResponse } from '@angular/common/http';
import { BodyStyleService } from './../../../service/bodyStyle.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { BodyStyle } from 'src/app/model/bodyStyle';

@Component({
  selector: 'app-edit-body-style-component',
  templateUrl: './edit-body-style-component.component.html',
  styleUrls: ['./edit-body-style-component.component.css']
})
export class EditBodyStyleComponent implements OnInit {
  editForm: FormGroup;

  constructor(private toastr: ToastrService, private bodyStyleService: BodyStyleService,
    private dialogRef: MatDialogRef<EditBodyStyleComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public selectedItem: BodyStyle) { }

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
    this.bodyStyleService.edit(new BodyStyle(this.editForm.value.name, this.selectedItem.id)).subscribe(
      (data: BodyStyle) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully edited a Body Style.', 'Edit Body Style');
        this.bodyStyleService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Body Style');
      }
    );
  }


}
