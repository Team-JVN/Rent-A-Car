import { HttpErrorResponse } from '@angular/common/http';
import { PriceListService } from './../../../service/price-list.service';
import { ToastrService } from 'ngx-toastr';
import { PriceList } from 'src/app/model/priceList';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-price-list',
  templateUrl: './edit-price-list.component.html',
  styleUrls: ['./edit-price-list.component.css']
})
export class EditPriceListComponent implements OnInit {

  editForm: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<EditPriceListComponent>,
    private formBuilder: FormBuilder,
    private toastr: ToastrService,
    private priceListService: PriceListService,
    @Inject(MAT_DIALOG_DATA) public selectedItem: PriceList
  ) { }

  ngOnInit() {
    this.editForm = this.formBuilder.group({
      pricePerDay: new FormControl(null, [Validators.required, Validators.min(1)]),
      pricePerKm: new FormControl(null, [Validators.min(1)]),
      priceForCDW: new FormControl(null, [Validators.min(1)]),
    });

    this.editForm.patchValue({
      'pricePerDay': this.selectedItem.pricePerDay,
      'pricePerKm': this.selectedItem.pricePerKm,
      'priceForCDW': this.selectedItem.priceForCDW,
    })
  }

  edit() {
    const priceList = new PriceList(this.editForm.value.pricePerDay, this.editForm.value.pricePerKm, this.editForm.value.priceForCDW, this.selectedItem.id);
    this.priceListService.edit(priceList).subscribe(
      (data: PriceList) => {
        this.editForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Edit Price List');
        this.priceListService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Edit Price List');
      }
    )
  }

}
