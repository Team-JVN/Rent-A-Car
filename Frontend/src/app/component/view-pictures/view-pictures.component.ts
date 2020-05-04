import { environment } from './../../../environments/environment';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../service/car.service';
import { CarWithPictures } from './../../model/carWithPictures';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, OnInit, Inject } from '@angular/core';
import { NgxGalleryOptions, NgxGalleryImage, NgxGalleryAnimation } from 'ngx-gallery';

@Component({
  selector: 'app-view-pictures',
  templateUrl: './view-pictures.component.html',
  styleUrls: ['./view-pictures.component.css']
})
export class ViewPicturesComponent implements OnInit {

  url = environment.baseUrl + environment.car;
  galleryOptions: NgxGalleryOptions[] = [];
  galleryImages: NgxGalleryImage[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public selectedCar: CarWithPictures
  ) { }

  ngOnInit() {
    this.galleryOptions = [
      {
        width: '100%',
        height: '500px',
        thumbnailsColumns: 5,
        thumbnailsPercent: 15,
        imagePercent: 85,
        imageAnimation: NgxGalleryAnimation.Slide,
        preview: false,
        imageInfinityMove: true,
        thumbnailsArrows: false,
        thumbnailsAutoHide: true,
      },

    ];

    this.fetchPictures();
  }

  fetchPictures() {
    this.selectedCar.pictures.forEach(element => {
      let imgUrl = this.url + '/' + this.selectedCar.id + '/picture?fileName=' + element.data;
      this.galleryImages.push({ small: imgUrl, medium: imgUrl });
    });
  }

}
