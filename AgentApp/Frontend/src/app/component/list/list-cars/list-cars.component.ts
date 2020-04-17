import { EditCarPartialComponent } from './../../edit/edit-car-partial/edit-car-partial.component';
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
import { NgxGalleryOptions, NgxGalleryImage, NgxGalleryAnimation } from 'ngx-gallery';

@Component({
  selector: 'app-list-cars',
  templateUrl: './list-cars.component.html',
  styleUrls: ['./list-cars.component.css']
})
export class ListCarsComponent implements OnInit {

  displayedColumns: string[] = ['car'];
  carsDataSource: MatTableDataSource<CarWithPicturesDTO>;
  successCreated: Subscription;

  galleryOptions: NgxGalleryOptions[];
  galleryImages: NgxGalleryImage[];

  constructor(public dialog: MatDialog,
    private carService: CarService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.galleryOptions = [
      {
        width: '600px',
        height: '400px',
        thumbnailsColumns: 4,
        imageAnimation: NgxGalleryAnimation.Slide
      },
      // max-width 800
      {
        breakpoint: 800,
        width: '100%',
        height: '600px',
        imagePercent: 80,
        thumbnailsPercent: 20,
        thumbnailsMargin: 20,
        thumbnailMargin: 20
      },
      // max-width 400
      {
        breakpoint: 400,
        preview: false
      }
    ];

    this.galleryImages = [
      {
        small: 'https://images3.polovniautomobili.tv/user-images/thumbs/1511/15113417/a8af7a5084ba-800x600.jpg',
        medium: 'https://images3.polovniautomobili.tv/user-images/thumbs/1511/15113417/a8af7a5084ba-800x600.jpg',
        big: 'https://images3.polovniautomobili.tv/user-images/thumbs/1511/15113417/a8af7a5084ba-800x600.jpg'
      },
      {
        small: 'https://www.car4rent.lv/images/3_prieksa_345x420.jpg',
        medium: 'https://www.car4rent.lv/images/3_prieksa_345x420.jpg',
        big: 'https://www.car4rent.lv/images/3_prieksa_345x420.jpg'
      },
      {
        small: 'https://images3.polovniautomobili.tv/user-images/thumbs/1511/15113417/6bb27a955657-800x600.jpg',
        medium: 'https://images3.polovniautomobili.tv/user-images/thumbs/1511/15113417/6bb27a955657-800x600.jpg',
        big: 'https://images3.polovniautomobili.tv/user-images/thumbs/1511/15113417/6bb27a955657-800x600.jpg'
      }
    ];


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
        console.log(data);
        this.createImageFromBlob(data, carWithPicturesDTO);
        carWithPicturesDTO.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
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
    this.carService.getEditType(element.carDTO.id).subscribe(
      (data: string) => {
        if (data === "ALL") {
          this.dialog.open(EditCarComponent, { data: element });
        } else {
          this.dialog.open(EditCarPartialComponent, { data: element });
        }
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: CarWithPicturesDTO[] = []
        this.carsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );

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
