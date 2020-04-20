import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../../service/car.service';
import { AdvertisementService } from './../../../service/advertisement.service';
import { ToastrService } from 'ngx-toastr';
import { AdvertisementWithPicturesDTO } from './../../../model/advertisementWithPictures';
import { Subscription } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddAdvertisementComponent } from '../../add/add-advertisement/add-advertisement.component';

@Component({
  selector: 'app-list-advertisements',
  templateUrl: './list-advertisements.component.html',
  styleUrls: ['./list-advertisements.component.css']
})
export class ListAdvertisementsComponent implements OnInit {

  displayedColumns: string[] = ['advertisement'];
  advertisementsDataSource: MatTableDataSource<AdvertisementWithPicturesDTO>;
  createSuccess: Subscription;

  constructor(
    public dialog: MatDialog,
    private advertisementService: AdvertisementService,
    private carService: CarService,
    private toastr: ToastrService
  ) { }

  ngOnInit() {
    this.fetchAll();
    this.createSuccess = this.advertisementService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAll()
      }
    );
  }

  fetchAll() {
    this.advertisementService.getAdvertisements().subscribe(
      (data: AdvertisementWithPicturesDTO[]) => {
        console.log(data);
        data.forEach(adWithPicturesDTO => {
          this.getPicture(adWithPicturesDTO);
        });
        this.advertisementsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: AdvertisementWithPicturesDTO[] = []
        this.advertisementsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Advertisements');
      }
    );
  }

  getPicture(adWithPicturesDTO: AdvertisementWithPicturesDTO) {
    this.carService.getPicture(adWithPicturesDTO.pictures[0], adWithPicturesDTO.advertisementDTO.car.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, adWithPicturesDTO);
        adWithPicturesDTO.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
      }
    );
  }

  createImageFromBlob(image: Blob, adWithPicturesDTO: AdvertisementWithPicturesDTO) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      adWithPicturesDTO.image = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  openDialog() {
    this.dialog.open(AddAdvertisementComponent);
  }

  edit(element: AdvertisementWithPicturesDTO) {

  }

  delete(element: AdvertisementWithPicturesDTO) {

  }

}
