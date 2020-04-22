import { NgxGalleryOptions, NgxGalleryImage, NgxGalleryAnimation } from 'ngx-gallery';
import { environment } from './../../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { AdvertisementWithPicturesDTO } from './../../model/advertisementWithPictures';
import { AdvertisementService } from './../../service/advertisement.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';

@Component({
  selector: 'app-advertisement-details',
  templateUrl: './advertisement-details.component.html',
  styleUrls: ['./advertisement-details.component.css']
})
export class AdvertisementDetailsComponent implements OnInit {

  advertisementId: number;
  selectedAdWithPictures: AdvertisementWithPicturesDTO;

  url = environment.baseUrl + environment.car;
  galleryOptions: NgxGalleryOptions[] = [];
  galleryImages: NgxGalleryImage[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastr: ToastrService,
    private advertisementService: AdvertisementService,
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
      this.advertisementService.get(this.advertisementId).subscribe(
        (data: AdvertisementWithPicturesDTO) => {
          this.selectedAdWithPictures = data;
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
    this.selectedAdWithPictures.pictures.forEach(element => {
      let imgUrl = this.url + '/' + this.selectedAdWithPictures.advertisement.car.id + '/picture?fileName=' + element;
      this.galleryImages.push({ small: imgUrl, medium: imgUrl });
    });
  }

}