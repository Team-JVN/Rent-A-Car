import { AdvertisementEdit } from './../../../model/advertisementEdit';
import { AddPriceListComponent } from './../../add/add-price-list/add-price-list.component';
import { AdvertisementWithPictures } from './../../../model/advertisementWithPictures';
import { PriceList } from './../../../model/priceList';
import { Subscription } from 'rxjs';
import { AdvertisementService } from './../../../service/advertisement.service';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { PriceListService } from 'src/app/service/price-list.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Advertisement } from 'src/app/model/advertisement';

@Component({
  selector: 'app-edit-advertisement-partial',
  templateUrl: './edit-advertisement-partial.component.html',
  styleUrls: ['./edit-advertisement-partial.component.css']
})
export class EditAdvertisementPartialComponent implements OnInit {
  priceListForm: FormGroup;
  dateForm: FormGroup;
  priceLists: PriceList[] = [];
  successCreatedList: Subscription;

  constructor(private toastr: ToastrService, private priceListService: PriceListService, private advertisementService: AdvertisementService,
    private dialogRef: MatDialogRef<EditAdvertisementPartialComponent>, @Inject(MAT_DIALOG_DATA) public selectedItem: AdvertisementWithPictures,
    private formBuilder: FormBuilder, public dialog: MatDialog) { }

  ngOnInit() {
    this.priceListForm = this.formBuilder.group({
      priceList: new FormControl(null, Validators.required)
    })

    this.dateForm = this.formBuilder.group({
      discount: new FormControl(this.selectedItem.discount, [Validators.min(0), Validators.max(99)]),
      kilometresLimit: new FormControl(this.selectedItem.kilometresLimit, Validators.min(1))
    })

    this.successCreatedList = this.priceListService.createSuccessEmitter.subscribe(
      () => {
        this.fetchPriceLists();
      }
    )

    this.fetchPriceLists();
  }


  fetchPriceLists() {
    this.priceListService.getAll().subscribe(
      (data: PriceList[]) => {
        this.priceLists = data;
        this.selectPriceList();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        const data: PriceList[] = []
        this.priceLists = data;
        this.toastr.error(httpErrorResponse.error.message, 'Display Price Lists');
      }
    )
  }

  selectPriceList() {
    this.priceLists.forEach((element: PriceList) => {
      if (element.id === this.selectedItem.priceList.id) {
        this.priceListForm.controls['priceList'].setValue(element);
      }
    });
  }


  getSelectedPriceList() {
    return this.priceListForm.get('priceList').value;
  }

  openAddPriceList() {
    this.dialog.open(AddPriceListComponent);
  }

  edit() {
    if (this.getSelectedPriceList().pricePerKm && !this.dateForm.value.kilometresLimit) {
      this.toastr.error("Please enter Kilometres limit", 'Create Advertisement');
      return;
    }

    var cdw = true;
    if (!this.priceListForm.value.priceList.priceForCDW) {
      cdw = false;
    }
    const advertisement = new AdvertisementEdit(this.priceListForm.value.priceList,
      this.dateForm.value.discount, this.dateForm.value.kilometresLimit);

    this.advertisementService.editPartial(advertisement, this.selectedItem.id).subscribe(
      (data: Advertisement) => {
        this.priceListForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success.', 'Edit Advertisement');
        this.advertisementService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Advertisement');
      }
    );
  }

}
