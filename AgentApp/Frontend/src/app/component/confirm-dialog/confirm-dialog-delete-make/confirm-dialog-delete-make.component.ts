import { HttpErrorResponse } from '@angular/common/http';
import { Make } from './../../../model/make';
import { ToastrService } from 'ngx-toastr';
import { MakeService } from './../../../service/make.service';
import { MatDialog, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog-delete-make',
  templateUrl: './confirm-dialog-delete-make.component.html',
  styleUrls: ['./confirm-dialog-delete-make.component.css']
})
export class ConfirmDialogDeleteMakeComponent implements OnInit {

  constructor(
    private makeService: MakeService, private dialogRef: MatDialogRef<ConfirmDialogDeleteMakeComponent>,
    private toastr: ToastrService, @Inject(MAT_DIALOG_DATA) public element: Make) { }

  ngOnInit() {
  }

  delete() {
    this.makeService.delete(this.element.id).subscribe(
      () => {
        this.toastr.success('Successfully deleted Make!', 'Delete Make');
        this.makeService.updateSuccessEmitter.next(this.element);
        this.dialogRef.close();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Make');
      }
    );
  }
}
