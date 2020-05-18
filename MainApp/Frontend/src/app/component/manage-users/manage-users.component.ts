import { Component, OnInit } from "@angular/core";
import {
  FormGroup,
  FormBuilder,
  FormControl,
  Validators,
} from "@angular/forms";
import { ToastrService } from "ngx-toastr";
import { AddAgentComponent } from "../add/add-agent/add-agent.component";
import { MatDialog } from "@angular/material/dialog";
import { AddAdminComponent } from "../add/add-admin/add-admin.component";
import { Subscription } from "rxjs";
import { ClientService } from "src/app/service/client.service";

@Component({
  selector: "app-manage-users",
  templateUrl: "./manage-users.component.html",
  styleUrls: ["./manage-users.component.css"],
})
export class ManageUsersComponent implements OnInit {
  status: string = "all";
  createSuccess: Subscription;
  displayedColumnsClient: string[] = [
    "name",
    "email",
    "address",
    "number",
    "canceledReservationCounter",
    "rejectedCommentsCounter",
    "delete",
    "block",
  ];
  displayedColumnsAgent: string[] = [
    "name",
    "email",
    "address",
    "number",
    "taxIdNumber",
    "delete",
  ];
  displayedColumnsAdmin: string[] = ["name", "email", "delete"];

  constructor(
    private toastr: ToastrService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private clientService: ClientService
  ) {}

  ngOnInit() {
    this.fetchAllClients("all");
    this.createSuccess = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAllClients(status);
      }
    );

    this.createSuccess = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAllClients(status);
      }
    );
  }
  fetchAllClients(status: string) {}
  newAgentDialog() {
    this.dialog.open(AddAgentComponent);
  }
  newAdminDialog() {
    this.dialog.open(AddAdminComponent);
  }
}
