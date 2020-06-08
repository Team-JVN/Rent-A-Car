import { AdvertisementService } from './../../../service/advertisement.service';
import { ViewPicturesComponent } from './../../view-pictures/view-pictures.component';
import { EditCarPartialComponent } from './../../edit/edit-car-partial/edit-car-partial.component';
import { CarWithPictures } from './../../../model/carWithPictures';
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
  carsDataSource: MatTableDataSource<CarWithPictures>;
  successCreated: Subscription;

  constructor(public dialog: MatDialog,
    private carService: CarService,
    private advertisementService: AdvertisementService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchCars();
    this.successCreated = this.carService.createSuccessEmitter.subscribe(
      () => {
        this.fetchCars()
      }
    );

  }

  getPicture(carWithPicturesDTO: CarWithPictures) {
    this.carService.getPicture(carWithPicturesDTO.pictures[0].data, carWithPicturesDTO.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, carWithPicturesDTO);
        carWithPicturesDTO.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
      }

    );
  }

  createImageFromBlob(image: Blob, carWithPicturesDTO: CarWithPictures) {
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
      (data: CarWithPictures[]) => {
        data.forEach(carWithPicturesDTO => {
          this.getPicture(carWithPicturesDTO);
        });
        this.carsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: CarWithPictures[] = []
        this.carsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );
  }

  edit(element: CarWithPictures) {
    this.advertisementService.getCarEditType(element.id).subscribe(
      (data: string) => {
        if (data === "ALL") {
          this.dialog.open(EditCarComponent, { data: element });
        } else {
          this.dialog.open(EditCarPartialComponent, { data: element });
        }
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: CarWithPictures[] = []
        this.carsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );

  }

  openDialog() {
    this.dialog.open(AddCarComponent);
  }

  delete(element: CarWithPictures) {
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

  viewPictures(element: CarWithPictures) {
    this.dialog.open(ViewPicturesComponent, { data: element });
  }

}
