import { AddAdminComponent } from './../../add/add-admin/add-admin.component';
import { AddAgentComponent } from './../../add/add-agent/add-agent.component';
import { element } from 'protractor';
import { BodyStyleService } from './../../../service/bodyStyle.service';
import { BodyStyle } from '../../../model/bodyStyle';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { AddBodyStyleComponent } from '../../add/add-body-style/add-body-style.component';
import { Subscription } from 'rxjs';
import { EditBodyStyleComponent } from '../../edit/edit-body-style-component/edit-body-style-component.component';

@Component({
  selector: 'app-list-body-styles',
  templateUrl: './list-body-styles.component.html',
  styleUrls: ['./list-body-styles.component.css']
})
export class ListBodyStylesComponent implements OnInit {

  displayedColumns: string[] = ['name', 'buttons'];
  bodyStylesDataSource: MatTableDataSource<BodyStyle>;
  successCreated: Subscription;

  constructor(public dialog: MatDialog,
    private bodyStyleService: BodyStyleService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchBodyStyles();
    this.successCreated = this.bodyStyleService.createSuccessEmitter.subscribe(
      () => {
        this.fetchBodyStyles()
      }
    );
  }

  fetchBodyStyles() {
    this.bodyStyleService.getBodyStyles().subscribe(
      (data: BodyStyle[]) => {
        this.bodyStylesDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: BodyStyle[] = []
        this.bodyStylesDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Body Styles');
      }
    );
  }

  edit(element: BodyStyle) {
    this.dialog.open(EditBodyStyleComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddBodyStyleComponent);
  }

  delete(element: BodyStyle) {
    this.bodyStyleService.delete(element.id).subscribe(
      () => {
        this.fetchBodyStyles();
        this.toastr.success('Successfully deleted Body Style!', 'Delete Body Style');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Body Style');
      }
    );
  }
}
