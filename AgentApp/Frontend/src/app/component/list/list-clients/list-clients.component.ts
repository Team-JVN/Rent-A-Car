import { AddClientComponent } from './../../add/add-client/add-client.component';
import { EditClientComponent } from './../../edit/edit-client/edit-client.component';
import { ToastrService } from 'ngx-toastr';
import { ClientService } from './../../../service/client.service';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Client } from './../../../model/client';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-list-clients',
  templateUrl: './list-clients.component.html',
  styleUrls: ['./list-clients.component.css']
})
export class ListClientsComponent implements OnInit {

  displayedColumns: string[] = ['name', 'email', 'address', 'phoneNumber', 'buttons'];
  clientsDataSource: MatTableDataSource<Client>;
  successCreated: Subscription;

  constructor(public dialog: MatDialog,
    private clientService: ClientService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchClients();
    this.successCreated = this.clientService.createSuccessEmitter.subscribe(
      () => {
        this.fetchClients()
      }
    );
  }

  fetchClients() {
    this.clientService.getClients().subscribe(
      (data: Client[]) => {
        this.clientsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Client[] = []
        this.clientsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Clients');
      }
    );
  }

  edit(element: Client) {
    this.dialog.open(EditClientComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddClientComponent);
  }

  delete(element: Client) {
    this.clientService.delete(element.id).subscribe(
      () => {
        this.fetchClients();
        this.toastr.success('Successfully deleted Client!', 'Delete Client');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Client');
      }
    );
  }

}
