
import { AdvertisementService } from 'src/app/service/advertisement.service';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { Location } from '@angular/common';
import { LocationInfo } from 'src/app/model/location.info';

@Component({
  selector: 'app-car-tracking',
  templateUrl: './car-tracking.component.html',
  styleUrls: ['./car-tracking.component.css']
})
export class CarTrackingComponent implements OnInit {

  advId: number;
  locationInfo: LocationInfo;

  constructor(private activatedRoute: ActivatedRoute, private location: Location,
    private router: Router, private advertisementService: AdvertisementService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.activatedRoute.params.subscribe((params: Params) => {
      this.advId = params['id'];
      this.startFetchingLocation();
    });
  }

  startFetchingLocation() {
    this.fetchLocation();
    this.delay(1100).then(any => {
      this.startFetchingLocation();
    });
  }

  fetchLocation() {
    this.advertisementService.getCarLocation(this.advId).subscribe(
      (data: LocationInfo) => {
        this.locationInfo = data;
        console.log(this.locationInfo);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Car location');
        this.location.back();
      }
    )
  }

  async delay(ms: number) {
    await new Promise(resolve => setTimeout(() => resolve(), ms));
  }
}
