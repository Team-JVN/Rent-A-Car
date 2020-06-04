import { AuthentificationService } from './../../service/authentification.service';
import { AdminService } from './../../service/admin.service';
import { ConfirmDialogDeleteAdminComponent } from './../confirm-dialog/confirm-dialog-delete-admin/confirm-dialog-delete-admin.component';
import { ConfirmDialogDeleteAgentComponent } from './../confirm-dialog/confirm-dialog-delete-agent/confirm-dialog-delete-agent.component';
import { Admin } from './../../model/admin';
import { Agent } from './../../model/agent';
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
import { AgentService } from 'src/app/service/agent.service';

@Component({
  selector: "app-manage-users",
  templateUrl: "./manage-users.component.html",
  styleUrls: ["./manage-users.component.css"],
})
export class ManageUsersComponent implements OnInit {
  status: string = "all";
  agentStatus: string = "all";
  adminStatus: string = "all";
  createAgentSuccess: Subscription;
  createAminSuccess: Subscription;

  rejectClientSuccess: Subscription;
  deleteClientSuccess: Subscription;
  deleteAgentSuccess: Subscription;
  deleteAdminSuccess: Subscription;

  clientsDataSource: MatTableDataSource<Client>;
  agentsDataSource: MatTableDataSource<Agent>;
  adminsDataSource: MatTableDataSource<Admin>;

  displayedColumnsClient: string[] = ["name", "email", "canceledReservationCounter", "rejectedCommentsCounter", "buttons"];
  displayedColumnsAgent: string[] = ["name", "email", "address", "number", "taxIdNumber", "buttons"];
  displayedColumnsAdmin: string[] = ["name", "email", "buttons"];

  constructor(private toastr: ToastrService, private formBuilder: FormBuilder, public dialog: MatDialog, private clientService: ClientService,
    private agentService: AgentService, public adminService: AdminService, public authentificationService: AuthentificationService) { }

  ngOnInit() {
    this.fetchAllClients("all");
    this.fetchAllAgents('all');
    this.fetchAllAdmins('all');

    this.createAgentSuccess = this.agentService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAllAgents('all');
      }
    );

    this.createAminSuccess = this.adminService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAllAdmins('all');
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

    this.deleteAgentSuccess = this.agentService.deleteSuccessEmitter.subscribe(
      () => {
        this.fetchAllAgents('all');
      }
    );

    this.deleteAdminSuccess = this.adminService.deleteSuccessEmitter.subscribe(
      () => {
        this.fetchAllAdmins('all');
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


  fetchAllAgents(status: string) {
    this.agentService.getAll(status).subscribe(
      (data: Agent[]) => {
        this.agentsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Agent[] = []
        this.agentsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show agents');
      }
    );
  }

  fetchAllAdmins(status: string) {
    this.adminService.getAll(status).subscribe(
      (data: Admin[]) => {
        this.adminsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Admin[] = []
        this.adminsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show admins');
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

  deleteAgent(element: Agent) {
    this.dialog.open(ConfirmDialogDeleteAgentComponent, { data: { agent: element } });
  }

  deleteAdmin(element: Admin) {
    this.dialog.open(ConfirmDialogDeleteAdminComponent, { data: { admin: element } });
  }

  newAgentDialog() {
    this.dialog.open(AddAgentComponent);
  }
  newAdminDialog() {
    this.dialog.open(AddAdminComponent);
  }
}
