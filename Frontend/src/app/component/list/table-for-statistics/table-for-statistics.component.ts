import { Component, OnInit, Input } from '@angular/core';
import { CarWithPictures } from 'src/app/model/carWithPictures';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { CarService } from 'src/app/service/car.service';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { ViewPicturesComponent } from '../../view-pictures/view-pictures.component';

@Component({
  selector: 'app-table-for-statistics',
  templateUrl: './table-for-statistics.component.html',
  styleUrls: ['./table-for-statistics.component.css']
})
export class TableForStatisticsComponent implements OnInit {
  @Input() statisticType: string;
  displayedColumns: string[] = ['car'];
  carsDataSource: MatTableDataSource<CarWithPictures>;

  constructor(public dialog: MatDialog,
    private carService: CarService,
    private toastr: ToastrService) { }

  ngOnInit() {
    this.fetchCars();
    this.fetchCarsForStatistics();
  }

  getPicture(carWithPicturesDTO: CarWithPictures) {
    this.carService.getPicture(carWithPicturesDTO.pictures[0], carWithPicturesDTO.carDTO.id).subscribe(
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

  fetchCars() {
    this.carService.getCars().subscribe(
      (data: CarWithPictures[]) => {
        data.forEach(carWithPicturesDTO => {
          this.getPicture(carWithPicturesDTO);
        });
        this.carsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );
  }

  fetchCarsForStatistics() {
    this.carService.getCarsForStatistics(this.statisticType).subscribe(
      (data: CarWithPictures[]) => {
        data.forEach(carWithPicturesDTO => {
          this.getPicture(carWithPicturesDTO);
        });
        this.carsDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Show Cars');
      }
    );
  }

  viewPictures(element: CarWithPictures) {
    this.dialog.open(ViewPicturesComponent, { data: element });
  }

}
