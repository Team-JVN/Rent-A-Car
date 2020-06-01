import { element } from 'protractor';
import { EditAdvertisementPartialComponent } from './../../edit/edit-advertisement-partial/edit-advertisement-partial.component';
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
import { AdvertisementFromSearch } from 'src/app/model/advertisementFromSearch';
import { SearchService } from 'src/app/service/search.service';

@Component({
  selector: 'app-list-advertisements',
  templateUrl: './list-advertisements.component.html',
  styleUrls: ['./list-advertisements.component.css']
})
export class ListAdvertisementsComponent implements OnInit {

  displayedColumns: string[] = ['advertisement'];
  advertisementsDataSource: MatTableDataSource<AdvertisementFromSearch>;
  createSuccess: Subscription;
  status: string = 'all';

  constructor(public router: Router, public dialog: MatDialog, private advertisementService: AdvertisementService, private carService: CarService,
    private toastr: ToastrService, private rentRequestService: RentRequestService, private searchService: SearchService) { }

  ngOnInit() {
    this.fetchAll('all');
    this.createSuccess = this.advertisementService.createSuccessEmitter.subscribe(
      () => {
        setTimeout(function () {
          this.fetchAll(this.status);
        }, 3000);

      }
    );

    this.createSuccess = this.rentRequestService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAll(this.status);
      }
    );
  }

  fetchAll(status: string) {
    this.searchService.getAllMy(status).subscribe(
      (data: AdvertisementFromSearch[]) => {
        data.forEach(adWithPicturesDTO => {
          this.getPicture(adWithPicturesDTO);
        });
        this.advertisementsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: AdvertisementFromSearch[] = []
        this.advertisementsDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Show Advertisements');
      }
    );
  }

  getPicture(adWithPicturesDTO: AdvertisementFromSearch) {
    this.carService.getPicture(adWithPicturesDTO.car.pictures[0], adWithPicturesDTO.car.id).subscribe(
      (data) => {
        this.createImageFromBlob(data, adWithPicturesDTO);
        adWithPicturesDTO.car.isImageLoading = false;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Get picture');
      }
    );
  }

  createImageFromBlob(image: Blob, adWithPicturesDTO: AdvertisementFromSearch) {
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

  edit(element: AdvertisementFromSearch) {
    // this.advertisementService.getEditType(element.id).subscribe(
    //   (data: string) => {
    //     if (data === "ALL") {
    //       this.dialog.open(EditAdvertisementComponent, { data: element });
    //     } else {
    //       this.dialog.open(EditAdvertisementPartialComponent, { data: element });
    //     }
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     this.toastr.error(httpErrorResponse.error.message, 'Get edit type');
    //   }
    // );
  }

  delete(element: AdvertisementFromSearch) {
    // this.advertisementService.delete(element.id).subscribe(
    //   () => {
    //     this.fetchAll(this.status);
    //     this.toastr.success('Successfully deleted Advertisement!', 'Delete Advertisement');
    //   },
    //   (httpErrorResponse: HttpErrorResponse) => {
    //     this.toastr.error(httpErrorResponse.error.message, 'Delete Advertisement');
    //   }
    // );
  }

  rent(element: AdvertisementFromSearch) {
    this.dialog.open(AddRentRequestComponent, { data: element });

  }

  viewDetails(element: AdvertisementFromSearch) {
    this.router.navigate(['/advertisement/' + element.id]);
  }

  checkIfCanRentAdvertisement(element: AdvertisementFromSearch): boolean {
    if (!element.dateTo) {
      return true;
    }
    if (new Date(element.dateTo) > new Date()) {
      return true;
    }
    return false;
  }

  viewRentRequests(element: AdvertisementFromSearch) {
    this.router.navigate(['/rent-requests/' + element.id]);
  }
}
