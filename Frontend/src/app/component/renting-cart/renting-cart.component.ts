import { CarService } from './../../service/car.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CarWithPictures } from './../../model/carWithPictures';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { RentInfo } from 'src/app/model/rentInfo';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-renting-cart',
  templateUrl: './renting-cart.component.html',
  styleUrls: ['./renting-cart.component.css']
})
export class RentingCartComponent implements OnInit {

  displayedColumns: string[] = ['rentInfo'];
  rentInfosDataSource: MatTableDataSource<RentInfo>;
  createSuccess: Subscription;

  constructor(
    public dialog: MatDialog,
    private toastr: ToastrService,
    private carService: CarService,
  ) { }

  ngOnInit() {
    // this.fetchRentInfos();
  }
  /*
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
  
    fetchRentInfos() {
      let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
      rentInfos.forEach(rentInfo => {
        this.getPicture(rentInfo.advertisement.car)
      })
      this.rentInfosDataSource = new MatTableDataSource(rentInfos);
    }*/

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

}
