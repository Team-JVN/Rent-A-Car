import { ConfirmDeleteClientComponent } from './../confirm-dialog/confirm-delete-client/confirm-delete-client.component';
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
import { RejectRequestToRegisterComponent } from '../reject-request-to-register/reject-request-to-register.component';

@Component({
  selector: "app-manage-users",
  templateUrl: "./manage-users.component.html",
  styleUrls: ["./manage-users.component.css"],
})
export class ManageUsersComponent implements OnInit {
  status: string = "all";
  createClientSuccess: Subscription;
  rejectClientSuccess: Subscription;
  deleteClientSuccess: Subscription;

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
    this.createClientSuccess = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAllClients('all');
      }
    );

    this.createClientSuccess = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAllClients('all');
      }
    );

    this.rejectClientSuccess = this.clientService.rejectSuccessEmitter.subscribe(
      () => {
        this.fetchAllClients('all');
      }
    );

    this.deleteClientSuccess = this.clientService.deleteSuccessEmitter.subscribe(
      () => {
        this.fetchAllClients('all');
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

  reject(element: Client) {
    this.dialog.open(RejectRequestToRegisterComponent, { data: { client: element } });
  }

  approve(element: Client) {
    this.clientService.approve(element.id).subscribe(
      () => {
        this.toastr.success("Success.", 'Approve client');
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Approve client');
      }
    );
  }

  delete(element: Client) {
    this.dialog.open(ConfirmDeleteClientComponent, { data: { client: element } });
  }

  block(element: Client) {
    this.clientService.block(element.id).subscribe(
      () => {
        this.toastr.success("Success.", 'Block client');
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Block client');
      }
    );
  }

  unblock(element: Client) {
    this.clientService.unblock(element.id).subscribe(
      () => {
        this.toastr.success("Success.", 'Unblock client');
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Unblock client');
      }
    );
  }

  disableCreatingRentRequests(element: Client) {
    this.clientService.creatingRentRequests(element.id, "disable").subscribe(
      () => {
        this.toastr.success("Success.", "Disable creating rent requests");
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Disable creating rent requests");
      }
    );
  }

  enableCreatingRentRequests(element: Client) {
    this.clientService.creatingRentRequests(element.id, "enable").subscribe(
      () => {
        this.toastr.success("Success.", 'Enable creating rent requests');
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Enable creating rent requests');
      }
    );
  }

  disableCreatingComments(element: Client) {
    this.clientService.creatingComments(element.id, "disable").subscribe(
      () => {
        this.toastr.success("Success.", "Disable creating comments");
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, "Disable creating comments");
      }
    );
  }

  enableCreatingComments(element: Client) {
    this.clientService.creatingComments(element.id, "enable").subscribe(
      () => {
        this.toastr.success("Success.", 'Enable creating comments');
        this.fetchAllClients(this.status);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Enable creating comments');
      }
    );
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
