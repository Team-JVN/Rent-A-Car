import { EditPriceListComponent } from './../../edit/edit-price-list/edit-price-list.component';
import { AddPriceListComponent } from './../../add/add-price-list/add-price-list.component';
import { PriceListService } from './../../../service/price-list.service';
import { PriceList } from './../../../model/priceList';
import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-list-price-lists',
  templateUrl: './list-price-lists.component.html',
  styleUrls: ['./list-price-lists.component.css']
})
export class ListPriceListsComponent implements OnInit {

  displayedColumns: string[] = ['pricePerDay', 'pricePerKm', 'priceForCDW', 'buttons'];
  priceListDataSource: MatTableDataSource<PriceList>;
  createSuccess: Subscription;

  constructor(
    public dialog: MatDialog,
    private toastr: ToastrService,
    private priceListService: PriceListService
  ) { }

  ngOnInit() {
    this.fetchAll();
    this.createSuccess = this.priceListService.createSuccessEmitter.subscribe(
      () => {
        this.fetchAll();
      }
    )
  }

  fetchAll() {
    this.priceListService.getAll().subscribe(
      (data: PriceList[]) => {
        this.priceListDataSource = new MatTableDataSource(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: PriceList[] = []
        this.priceListDataSource = new MatTableDataSource(data)
        this.toastr.error(httpErrorResponse.error.message, 'Display Price Lists');
      }
    )
  }

  openCreateDialog() {
    this.dialog.open(AddPriceListComponent);
  }

  edit(item: PriceList) {
    this.dialog.open(EditPriceListComponent, { data: item });
  }

  delete(item: PriceList) {
    this.priceListService.delete(item.id).subscribe(
      () => {
        this.fetchAll();
        this.toastr.success('Success!', 'Delete Price List');
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Delete Price List');
      }
    );
  }

}
