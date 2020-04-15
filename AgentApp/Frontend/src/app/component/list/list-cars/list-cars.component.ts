import { CarWithPicturesDTO } from './../../../model/carWithPictures';
import { AddCarComponent } from './../../add/add-car/add-car.component';
import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../../service/car.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';
import { EditCarComponent } from '../../edit/edit-car/edit-car.component';

@Component({
  selector: 'app-list-cars',
  templateUrl: './list-cars.component.html',
  styleUrls: ['./list-cars.component.css']
})
export class ListCarsComponent implements OnInit {

  displayedColumns: string[] = ['car'];
  carsDataSource: MatTableDataSource<CarWithPicturesDTO>;
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

  getPicture(carWithPicturesDTO: CarWithPicturesDTO) {
    this.carService.getPicture(carWithPicturesDTO.pictures[0], carWithPicturesDTO.carDTO.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, carWithPicturesDTO);
        carWithPicturesDTO.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Car');
      }

    );
  }

  createImageFromBlob(image: Blob, carWithPicturesDTO: CarWithPicturesDTO) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      carWithPicturesDTO.image = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  fetchCars() {
    this.carService.getCars().subscribe(
      (data: CarWithPicturesDTO[]) => {
        data.forEach(carWithPicturesDTO => {
          this.getPicture(carWithPicturesDTO);
        });
        this.carsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: CarWithPicturesDTO[] = []
        this.carsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );
  }

  edit(element: CarWithPicturesDTO) {
    this.dialog.open(EditCarComponent, { data: element });
  }

  openDialog() {
    this.dialog.open(AddCarComponent);
  }

  delete(element: CarWithPicturesDTO) {
    this.carService.delete(element.carDTO.id).subscribe(
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
