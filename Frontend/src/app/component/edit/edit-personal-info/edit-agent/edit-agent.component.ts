import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";

@Component({
  selector: "app-edit-agent",
  templateUrl: "./edit-agent.component.html",
  styleUrls: ["./edit-agent.component.css"],
})
export class EditAgentComponent implements OnInit {
  editForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      address: new FormControl(null, Validators.required),
      phoneNumber: new FormControl(null, [
        Validators.required,
        Validators.minLength(9),
        Validators.maxLength(10),
        Validators.pattern("0[0-9]+"),
      ]),
    });
  }
  editAgent() {}
}
