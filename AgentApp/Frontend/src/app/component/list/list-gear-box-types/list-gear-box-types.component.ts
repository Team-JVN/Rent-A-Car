import { AddGearBoxTypeComponent } from './../../add/add-gear-box-type/add-gear-box-type.component';
import { EditGearBoxTypeComponent } from './../../edit/edit-gear-box-type/edit-gear-box-type.component';
import { HttpErrorResponse } from '@angular/common/http';
import { GearboxTypeService } from './../../../service/gearboxType.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { GearBoxType } from './../../../model/gearboxType';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-gear-box-types',
  templateUrl: './list-gear-box-types.component.html',
  styleUrls: ['./list-gear-box-types.component.css']
})
export class ListGearBoxTypesComponent implements OnInit {
  displayedColumns: string[] = ['name', 'buttons'];
  gearboxTypesDataSource: MatTableDataSource<GearBoxType>;
  successCreated: Subscription;

  constructor(public dialog: MatDialog,
    private gearboxTypeService: GearboxTypeService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchGearboxTypes();

    this.successCreated = this.gearboxTypeService.createSuccessEmitter.subscribe(
      () => {
        this.fetchGearboxTypes()
      }
    );
  }

  fetchGearboxTypes() {
    this.gearboxTypeService.getGearboxTypes().subscribe(
      (data: GearBoxType[]) => {
        this.gearboxTypesDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: GearBoxType[] = []
        this.gearboxTypesDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Gearbox Types');
      }
    );
  }

  edit(element: GearBoxType) {
    this.dialog.open(EditGearBoxTypeComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddGearBoxTypeComponent);
  }

  delete(element: GearBoxType) {
    this.gearboxTypeService.delete(element.id).subscribe(
      () => {
        this.fetchGearboxTypes();
        this.toastr.success('Successfully deleted Gearbox Types!', 'Delete Gearbox Types');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Gearbox Types');
      }
    );
  }

}
