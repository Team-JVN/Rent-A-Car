import { AddFuelTypeComponent } from './../../add/add-fuel-type/add-fuel-type.component';
import { EditFuelTypeComponent } from './../../edit/edit-fuel-type/edit-fuel-type.component';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { FuelType } from './../../../model/fuelType';
import { MatTableDataSource } from '@angular/material/table';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { FuelTypeService } from './../../../service/fuelType.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-list-fuel-type',
  templateUrl: './list-fuel-type.component.html',
  styleUrls: ['./list-fuel-type.component.css']
})
export class ListFuelTypeComponent implements OnInit {
  displayedColumns: string[] = ['name', 'buttons'];
  fuelTypesDataSource: MatTableDataSource<FuelType>;
  successCreated: Subscription;

  constructor(public dialog: MatDialog,
    private fuelTypeService: FuelTypeService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchFuelTypes();

    this.successCreated = this.fuelTypeService.createSuccessEmitter.subscribe(
      () => {
        this.fetchFuelTypes()
      }
    );
  }

  fetchFuelTypes() {
    this.fuelTypeService.getFuelTypes().subscribe(
      (data: FuelType[]) => {
        this.fuelTypesDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: FuelType[] = []
        this.fuelTypesDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Fuel Types');
      }
    );
  }

  edit(element: FuelType) {
    this.dialog.open(EditFuelTypeComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddFuelTypeComponent);
  }

  delete(element: FuelType) {
    this.fuelTypeService.delete(element.id).subscribe(
      () => {
        this.fetchFuelTypes();
        this.toastr.success('Successfully deleted Fuel Types!', 'Delete Fuel Types');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Fuel Types');
      }
    );
  }

}
