import { ClientService } from './../../service/client.service';
import { Client } from './../../model/client';
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
import { MatTableDataSource } from '@angular/material/table';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: "app-manage-users",
  templateUrl: "./manage-users.component.html",
  styleUrls: ["./manage-users.component.css"],
})
export class ManageUsersComponent implements OnInit {
  status: string = "all";
  createSuccess: Subscription;
  clientsDataSource: MatTableDataSource<Client>;

  displayedColumnsClient: string[] = [
    "name",
    "email",
    "canceledReservationCounter",
    "rejectedCommentsCounter",
    "buttons"
  ];
  displayedColumnsAgent: string[] = [
    "name",
    "email",
    "address",
    "number",
    "taxIdNumber",
    "buttons",
  ];
  displayedColumnsAdmin: string[] = ["name", "email", "buttons"];

  constructor(
    private toastr: ToastrService,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private clientService: ClientService
  ) { }

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

  fetchAllClients(status: string) {
    this.clientService.getAll(status).subscribe(
      (data: Client[]) => {
        this.clientsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Client[] = []
        this.clientsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show clients');
      }
    );
  }

  reject(element) {

  }

  approve(element: Client) {
    this.clientService.approve(element.id).subscribe(
      () => {
        this.toastr.success("Success.", 'Approve client');
        this.fetchAllClients("all");
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Approve client');
      }
    );
  }
  delete(element) {

  }

  deleteAgent(element) {

  }

  deleteAdmin(element) {

  }
  newAgentDialog() {
    this.dialog.open(AddAgentComponent);
  }
  newAdminDialog() {
    this.dialog.open(AddAdminComponent);
  }
}
