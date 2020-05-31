import { AdminService } from './../../../service/admin.service';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog-delete-admin',
  templateUrl: './confirm-dialog-delete-admin.component.html',
  styleUrls: ['./confirm-dialog-delete-admin.component.css']
})
export class ConfirmDialogDeleteAdminComponent implements OnInit {
  constructor(
    public toastr: ToastrService, private adminService: AdminService, public dialogRef: MatDialogRef<ConfirmDialogDeleteAdminComponent>,
    @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
  }

  delete(): void {
    this.adminService.delete(this.data.admin.id).subscribe(
      () => {
        this.dialogRef.close();
        this.toastr.success('Success. ', 'Delete admin');
        this.adminService.deleteSuccessEmitter.next(this.data.admin);
      },
      () => {
        this.toastr.error('Something goes wrong. Please try again.', 'Delete admin');
        this.dialogRef.close();
      }
    )
  }

}
