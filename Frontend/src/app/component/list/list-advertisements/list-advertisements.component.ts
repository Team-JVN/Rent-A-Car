import { DateTime } from 'luxon';
import { RentRequestService } from 'src/app/service/rent-request.service';
import { AddRentRequestComponent } from './../../add/add-rent-request/add-rent-request.component';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../../service/car.service';
import { AdvertisementService } from './../../../service/advertisement.service';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddAdvertisementComponent } from '../../add/add-advertisement/add-advertisement.component';
import { MatTableDataSource } from '@angular/material/table';
import { AdvertisementWithPictures } from 'src/app/model/advertisementWithPictures';
import { EditAdvertisementComponent } from '../../edit/edit-advertisement/edit-advertisement.component';

@Component({
  selector: 'app-list-advertisements',
  templateUrl: './list-advertisements.component.html',
  styleUrls: ['./list-advertisements.component.css']
})
export class ListAdvertisementsComponent implements OnInit {

  displayedColumns: string[] = ['advertisement'];
  advertisementsDataSource: MatTableDataSource<AdvertisementWithPictures>;
  createSuccess: Subscription;
  status: string = 'all';

  constructor(
    public router: Router,
    public dialog: MatDialog,
    private advertisementService: AdvertisementService,
    private carService: CarService,
    private toastr: ToastrService,
    private rentRequestService: RentRequestService
  ) { }

  ngOnInit() {
    this.fetchAll('all');
    this.createSuccess = this.advertisementService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAll(this.status)
      }
    );

    this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAll(this.status);
      }
    );
  }

  fetchAll(status: string) {
    this.advertisementService.getAll(status).subscribe(
      (data: AdvertisementWithPictures[]) => {
        data.forEach(adWithPicturesDTO => {
          this.getPicture(adWithPicturesDTO);
        });
        this.advertisementsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: AdvertisementWithPictures[] = []
        this.advertisementsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Advertisements');
      }
    );
  }

  getPicture(adWithPicturesDTO: AdvertisementWithPictures) {
    this.carService.getPicture(adWithPicturesDTO.car.pictures[0].data, adWithPicturesDTO.car.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, adWithPicturesDTO);
        adWithPicturesDTO.car.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
      }
    );
  }

  createImageFromBlob(image: Blob, adWithPicturesDTO: AdvertisementWithPictures) {
    let reader = new FileReader();
    reader.addEventListener("load", () => {
      adWithPicturesDTO.car.image = reader.result;
    }, false);

    if (image) {
      reader.readAsDataURL(image);
    }
  }

  openDialog() {
    this.dialog.open(AddAdvertisementComponent);
  }

  edit(element: AdvertisementWithPictures) {
    this.dialog.open(EditAdvertisementComponent, { data: element });

  }

  delete(element: AdvertisementWithPictures) {
    this.advertisementService.delete(element.id).subscribe(
      () => {
        this.fetchAll(this.status);
        this.toastr.success('Successfully deleted Advertisement!', 'Delete Advertisement');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Advertisement');
      }
    );
  }

  rent(element: AdvertisementWithPictures) {
    this.dialog.open(AddRentRequestComponent, { data: element });

  }

  viewDetails(element: AdvertisementWithPictures) {
    this.router.navigate(['/advertisement/' + element.id]);
  }

  checkIfCanRentAdvertisement(element: AdvertisementWithPictures): boolean {
    if (!element.dateTo) {
      return true;
    }
    if (new Date(element.dateTo) > new Date()) {
      return true;
    }
    return false;
  }
}
