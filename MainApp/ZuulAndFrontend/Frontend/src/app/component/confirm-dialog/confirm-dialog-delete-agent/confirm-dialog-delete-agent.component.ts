import { AgentService } from './../../../service/agent.service';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog-delete-agent',
  templateUrl: './confirm-dialog-delete-agent.component.html',
  styleUrls: ['./confirm-dialog-delete-agent.component.css']
})
export class ConfirmDialogDeleteAgentComponent implements OnInit {
  constructor(
    public toastr: ToastrService, private agentService: AgentService, public dialogRef: MatDialogRef<ConfirmDialogDeleteAgentComponent>,
    @Inject(MAT_DIALOG_DATA) public data) { }

  ngOnInit(): void {
  }

  delete(): void {
    this.agentService.delete(this.data.agent.id).subscribe(
      () => {
        this.dialogRef.close();
        this.toastr.success('Success. ', 'Delete agent');
        this.agentService.deleteSuccessEmitter.next(this.data.agent);
      },
      () => {
        this.toastr.error('Something goes wrong. Please try again.', 'Delete agent');
        this.dialogRef.close();
      }
    )
  }

}
