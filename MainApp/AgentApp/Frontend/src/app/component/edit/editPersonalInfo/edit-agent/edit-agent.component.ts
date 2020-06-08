import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Agent } from './../../../../model/agent';
import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { AgentService } from 'src/app/service/agent.service';


@Component({
  selector: 'app-edit-agent',
  templateUrl: './edit-agent.component.html',
  styleUrls: ['./edit-agent.component.css']
})
export class EditAgentComponent implements OnInit {
  editForm: FormGroup;
  loggedInAgent: Agent = new Agent("", "", "", "", 0);

  constructor(private formBuilder: FormBuilder, private agentService: AgentService, private toastr: ToastrService) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      name: new FormControl(null, Validators.required),
      address: new FormControl(null, Validators.required),
      phoneNumber: new FormControl(null, [
        Validators.required,
        Validators.minLength(9),
        Validators.maxLength(10),
        Validators.pattern("0[0-9]+"),
      ]),
      taxIdNumber: new FormControl(null, [Validators.required, Validators.pattern("[1-9][0-9]+"), Validators.maxLength(9), Validators.minLength(9)])
    });

    this.agentService.getLoggedInUser().subscribe(
      (responseData: Agent) => {
        this.loggedInAgent = responseData;

        this.editForm.patchValue(
          {
            'name': this.loggedInAgent.name,
            'address': this.loggedInAgent.address,
            'phoneNumber': this.loggedInAgent.phoneNumber,
            'taxIdNumber': this.loggedInAgent.taxIdNumber
          }
        );
      },
      () => {
        this.toastr.error('Something went wrong. Please try again.', 'Edit profile');
      }
    );
  }

  editAgent() {
    this.agentService.edit(new Agent(this.editForm.value.name, this.loggedInAgent.email, this.editForm.value.address, this.editForm.value.phoneNumber,
      this.editForm.value.taxIdNumber)).subscribe(
        () => {
          this.toastr.success("Success.", "Edit personal info");
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, "Edit personal info");
        }
      );
  }

}
