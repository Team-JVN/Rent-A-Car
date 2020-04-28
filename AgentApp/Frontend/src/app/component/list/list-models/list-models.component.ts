import { MakeService } from './../../../service/make.service';
import { AddModelComponent } from './../../add/add-model/add-model.component';
import { EditModelComponent } from './../../edit/edit-model/edit-model.component';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';
import { Model } from 'src/app/model/model';
import { Location } from '@angular/common';

@Component({
  selector: 'app-list-models',
  templateUrl: './list-models.component.html',
  styleUrls: ['./list-models.component.css']
})
export class ListModelsComponent implements OnInit {

  displayedColumns: string[] = ['name', 'buttons'];
  modelsDataSource: MatTableDataSource<Model>;
  successCreated: Subscription;
  makeId: number;

  constructor(public dialog: MatDialog, private router: Router,
    private makeService: MakeService, private route: ActivatedRoute,
    private toastr: ToastrService, private location: Location) { }

  ngOnInit() {
    this.route.params.subscribe(
      (params: Params) => {
        this.makeId = params['id'];

        if (!this.makeId) {
          this.location.back();
          return;
        }
      }
    );

    this.fetchModels();
    this.successCreated = this.makeService.createModelSuccessEmitter.subscribe(
      () => {
        this.fetchModels()
      }
    );
  }

  fetchModels() {
    this.makeService.getModels(this.makeId).subscribe(
      (data: Model[]) => {
        this.modelsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Model[] = []
        this.modelsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Models');
      }
    );
  }

  edit(element: Model) {
    this.dialog.open(EditModelComponent, { data: { model: element, makeId: this.makeId } });
  }

  openDialog() {
    this.dialog.open(AddModelComponent, { data: this.makeId });
  }

  delete(element: Model) {
    this.makeService.deleteModel(this.makeId, element.id).subscribe(
      () => {
        this.fetchModels();
        this.toastr.success('Successfully deleted Model!', 'Delete Model');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Model');
      }
    );
  }


}
