import { EditAdvertisementComponent } from './../../edit/edit-advertisement/edit-advertisement.component';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddAdvertisementComponent } from '../../add/add-advertisement/add-advertisement.component';
import { MatTableDataSource } from '@angular/material/table';
import { AdvertisementWithPictures } from 'src/app/model/advertisementWithPictures';

@Component({
  selector: 'app-list-advertisements',
  templateUrl: './list-advertisements.component.html',
  styleUrls: ['./list-advertisements.component.css']
})
export class ListAdvertisementsComponent implements OnInit {
  advertisementsDataSource: MatTableDataSource<AdvertisementWithPictures>;
  constructor(public dialog: MatDialog) { }

  ngOnInit() {
  }

  openDialog() {
    this.dialog.open(AddAdvertisementComponent);
  }

  edit(element: AdvertisementWithPictures) {
    this.dialog.open(EditAdvertisementComponent, { data: element });
  }

}
