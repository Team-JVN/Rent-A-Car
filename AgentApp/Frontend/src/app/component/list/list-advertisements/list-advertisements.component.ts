import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AddAdvertisementComponent } from '../../add/add-advertisement/add-advertisement.component';

@Component({
  selector: 'app-list-advertisements',
  templateUrl: './list-advertisements.component.html',
  styleUrls: ['./list-advertisements.component.css']
})
export class ListAdvertisementsComponent implements OnInit {

  constructor(public dialog: MatDialog) { }

  ngOnInit() {
  }

  openDialog() {
    this.dialog.open(AddAdvertisementComponent);
  }

}
