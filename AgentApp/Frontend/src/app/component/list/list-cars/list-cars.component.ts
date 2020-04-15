import { AddCarComponent } from './../../add/add-car/add-car.component';
import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../../service/car.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { Car } from './../../../model/car';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';
import { EditCarComponent } from '../../edit/edit-car/edit-car.component';

@Component({
  selector: 'app-list-cars',
  templateUrl: './list-cars.component.html',
  styleUrls: ['./list-cars.component.css']
})
export class ListCarsComponent implements OnInit {
  displayedColumns: string[] = ['make', 'model', 'fuelType', 'gearBoxType', 'bodyStyle', 'mileageInKm', 'kidsSeats', 'availableTracking', 'buttons'];
  carsDataSource: MatTableDataSource<Car>;
  successCreated: Subscription;

  constructor(public dialog: MatDialog,
    private carService: CarService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchCars();
    this.successCreated = this.carService.createSuccessEmitter.subscribe(
      () => {
        this.fetchCars()
      }
    );
  }

  fetchCars() {
    this.carService.getCars().subscribe(
      (data: Car[]) => {
        this.carsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: Car[] = []
        this.carsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );
  }

  edit(element: Car) {
    this.dialog.open(EditCarComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddCarComponent);
  }

  delete(element: Car) {
    this.carService.delete(element.id).subscribe(
      () => {
        this.fetchCars();
        this.toastr.success('Successfully deleted Car!', 'Delete Car');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Car');
      }
    );
  }
}
