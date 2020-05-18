import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { ClientService } from "src/app/service/client.service";

@Component({
  selector: "app-edit-client-info",
  templateUrl: "./edit-client-info.component.html",
  styleUrls: ["./edit-client-info.component.css"],
})
export class EditClientInfoComponent implements OnInit {
  editForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private clientService: ClientService
  ) {}

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
  editClient() {}
}
