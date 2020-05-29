import { Agent } from './../../../model/agent';
import { AgentService } from './../../../service/agent.service';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-add-agent',
  templateUrl: './add-agent.component.html',
  styleUrls: ['./add-agent.component.css']
})
export class AddAgentComponent implements OnInit {
  addForm: FormGroup;

  constructor(private toastr: ToastrService, private agentService: AgentService,
    private formBuilder: FormBuilder, private dialogRef: MatDialogRef<AddAgentComponent>, ) { }

  ngOnInit() {
    this.addForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
      name: new FormControl(null, Validators.required),
      taxIdNumber: new FormControl(null, [Validators.required, Validators.pattern("[1-9][0-9]+"), Validators.maxLength(9), Validators.minLength(9)]),
      address: new FormControl(null, Validators.required),
      phoneNumber: new FormControl(null, [Validators.required, Validators.minLength(9), Validators.maxLength(10), Validators.pattern("0[0-9]+")]),
    })
  }

  register() {
    if (this.addForm.invalid) {
      this.toastr.error("Please enter valid data", 'Registrate Agent/Company');
      return;
    }
    const agent = new Agent(this.addForm.value.name, this.addForm.value.email, this.addForm.value.address,
      this.addForm.value.phoneNumber, this.addForm.value.taxIdNumber)
    this.agentService.add(agent).subscribe(
      (data: Agent) => {
        this.addForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Registrate Agent/Company');
        this.agentService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Registrate Agent/Company');
      }
    );
  }
}
