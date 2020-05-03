import { ViewPicturesComponent } from './../../view-pictures/view-pictures.component';
import { CarWithPictures } from './../../../model/carWithPictures';
import { HttpErrorResponse } from '@angular/common/http';
import { CarService } from './../../../service/car.service';
import { ToastrService } from 'ngx-toastr';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-cars-statistics',
  templateUrl: './cars-statistics.component.html',
  styleUrls: ['./cars-statistics.component.css']
})
export class CarsStatisticsComponent implements OnInit {
  constructor() {
  }

  ngOnInit() {
  }
}
