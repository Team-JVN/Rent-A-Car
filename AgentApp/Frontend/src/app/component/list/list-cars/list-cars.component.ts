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
import { FuelType } from 'src/app/model/fuelType';
import { GearBoxType } from 'src/app/model/gearboxType';
import { BodyStyle } from 'src/app/model/bodyStyle';

@Component({
  selector: 'app-list-cars',
  templateUrl: './list-cars.component.html',
  styleUrls: ['./list-cars.component.css']
})
export class ListCarsComponent implements OnInit {

  displayedColumns: string[] = ['car'];
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

    const car1 = new Car("Opel", "Astra", new FuelType("Petrol"), new GearBoxType("6-speed Manual"), new BodyStyle("Hatchback"), 68500, 2, true);
    const car2 = new Car("Ford", "Fiesta", new FuelType("Petrol"), new GearBoxType("5-speed Manual"), new BodyStyle("Hatchback"), 141000, 1, false);
    const car3 = new Car("VW", "Passat", new FuelType("Diesel"), new GearBoxType("6-speed Automatic"), new BodyStyle("Limousine"), 125000, 3, true);
    const car4 = new Car("BMW", "X5", new FuelType("Diesel"), new GearBoxType("7-speed Automatic"), new BodyStyle("SUV"), 30400, 3, true);
    this.carsDataSource = new MatTableDataSource([car1, car2, car3, car4]);

    // this.carService.getCars().subscribe(
    //   (data: Car[]) => {
    //     this.carsDataSource = new MatTableDataSource(data);
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     const data: Car[] = []
    //     this.carsDataSource = new MatTableDataSource(data)
    //     this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
    //   }
    // );
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
