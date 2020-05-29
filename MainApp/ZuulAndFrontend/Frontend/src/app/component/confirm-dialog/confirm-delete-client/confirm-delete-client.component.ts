import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { ClientService } from 'src/app/service/client.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-delete-client',
  templateUrl: './confirm-delete-client.component.html',
  styleUrls: ['./confirm-delete-client.component.css']
})
export class ConfirmDeleteClientComponent implements OnInit {

  constructor(
    public toastr: ToastrService, private clientService: ClientService, public dialogRef: MatDialogRef<ConfirmDeleteClientComponent>,
    @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
  }

  delete(): void {
    this.clientService.delete(this.data.client.id).subscribe(
      () => {
        this.dialogRef.close();
        this.toastr.success('Success.', 'Delete client');
        this.clientService.deleteSuccessEmitter.next(this.data.client);
      },
      () => {
        this.toastr.error('Something goes wrong. Please try again.', 'Delete client');
        this.dialogRef.close();
      }
    )
  }
}
