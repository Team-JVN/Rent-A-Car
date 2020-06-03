import { AuthentificationService } from './../../service/authentification.service';
import { SearchService } from 'src/app/service/search.service';
import { AdvertisementFromSearch } from './../../model/advertisementFromSearch';
import { NgxGalleryOptions, NgxGalleryImage, NgxGalleryAnimation } from 'ngx-gallery';
import { environment } from './../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-advertisement-details',
  templateUrl: './advertisement-details.component.html',
  styleUrls: ['./advertisement-details.component.css']
})
export class AdvertisementDetailsComponent implements OnInit {

  advertisementId: number;
  selectedAdvertisement: AdvertisementFromSearch;

  url = environment.baseUrl + environment.car;
  galleryOptions: NgxGalleryOptions[] = [];
  galleryImages: NgxGalleryImage[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private searchService: SearchService,
    private authService: AuthentificationService,
    public dialog: MatDialog,
  ) { }

  ngOnInit() {
    this.galleryOptions = [{
      width: '540px',
      height: '400px',
      thumbnailsColumns: 5,
      thumbnailsPercent: 15,
      imagePercent: 85,
      imageAnimation: NgxGalleryAnimation.Slide,
      preview: false,
      imageInfinityMove: true,
      thumbnailsArrows: false,
      thumbnailsAutoHide: true,
    }];

    this.activatedRoute.params.subscribe((params: Params) => {
      this.advertisementId = params['id'];
      this.searchService.get(this.advertisementId).subscribe(
        (data: AdvertisementFromSearch) => {
          this.selectedAdvertisement = data;
          this.fetchPictures();
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, 'Advertisement Details');
          this.router.navigate(['']);
        }
      )
    });
  }

  fetchPictures() {
    this.selectedAdvertisement.car.pictures.forEach(pictureName => {
      let imgUrl = this.url + '/' + this.selectedAdvertisement.car.id + '/picture?fileName=' + pictureName;
      this.galleryImages.push({ small: imgUrl, medium: imgUrl });
    });
  }
}
