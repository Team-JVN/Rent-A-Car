import { Make } from './../../../model/make';
import { MakeService } from './../../../service/make.service';
import { Component, OnInit, Inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
@Component({
  selector: 'app-edit-make',
  templateUrl: './edit-make.component.html',
  styleUrls: ['./edit-make.component.css']
})
export class EditMakeComponent implements OnInit {
  editForm: FormGroup;

  constructor(private toastr: ToastrService, private makeService: MakeService,
    private dialogRef: MatDialogRef<EditMakeComponent>, private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public selectedItem: Make) { }

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
    this.makeService.edit(new Make(this.editForm.value.name, this.selectedItem.id)).subscribe(
      (data: Make) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Successfully edited a Make.', 'Edit Make');
        this.makeService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Make');
      }
    );
  }


}
