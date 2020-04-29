import { Router } from '@angular/router';
import { ConfirmDialogDeleteMakeComponent } from './../../confirm-dialog/confirm-dialog-delete-make/confirm-dialog-delete-make.component';
import { AddMakeComponent } from './../../add/add-make/add-make.component';
import { EditMakeComponent } from './../../edit/edit-make/edit-make.component';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { MakeService } from './../../../service/make.service';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';
import { Make } from 'src/app/model/make';

@Component({
  selector: 'app-list-makes',
  templateUrl: './list-makes.component.html',
  styleUrls: ['./list-makes.component.css']
})
export class ListMakesComponent implements OnInit {

  displayedColumns: string[] = ['name', 'buttons'];
  makesDataSource: MatTableDataSource<Make>;
  successCreated: Subscription;
  successUpdated: Subscription;

  constructor(public dialog: MatDialog, private router: Router,
    private makeService: MakeService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchMakes();
    this.successCreated = this.makeService.createSuccessEmitter.subscribe(
      () => {
        this.fetchMakes()
      }
    );
    this.successUpdated = this.makeService.updateSuccessEmitter.subscribe(
      () => {
        this.fetchMakes()
      }
    );
  }

  fetchMakes() {
    this.makeService.getMakes().subscribe(
      (data: Make[]) => {
        this.makesDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Make[] = []
        this.makesDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Makes');
      }
    );
  }

  edit(element: Make) {
    this.dialog.open(EditMakeComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddMakeComponent);
  }

  delete(element: Make) {
    this.dialog.open(ConfirmDialogDeleteMakeComponent, { data: element });
  }

  viewModels(element: Make) {
    this.router.navigate(['/models/' + element.id]);
  }
}
