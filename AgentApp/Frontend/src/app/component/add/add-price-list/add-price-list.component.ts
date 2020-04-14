import { HttpErrorResponse } from '@angular/common/http';
import { PriceListService } from './../../../service/price-list.service';
import { ToastrService } from 'ngx-toastr';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { PriceList } from 'src/app/model/priceList';

@Component({
  selector: 'app-add-price-list',
  templateUrl: './add-price-list.component.html',
  styleUrls: ['./add-price-list.component.css']
})
export class AddPriceListComponent implements OnInit {

  createForm: FormGroup;

  constructor(
    private dialogRef: MatDialogRef<AddPriceListComponent>,
    private formBuilder: FormBuilder,
    private toastr: ToastrService,
    private priceListService: PriceListService,
  ) { }

  ngOnInit() {
    this.createForm = this.formBuilder.group({
      pricePerDay: new FormControl(null, [Validators.required, Validators.min(1)]),
      pricePerKm: new FormControl(null, [Validators.min(1)]),
      priceForCDW: new FormControl(null, [Validators.min(1)]),
    })
  }

  create() {
    const priceList = new PriceList(this.createForm.value.pricePerDay, this.createForm.value.pricePerKm, this.createForm.value.priceForCDW);
    this.priceListService.create(priceList).subscribe(
      (data: PriceList) => {
        this.createForm.reset();
        this.dialogRef.close();
        this.toastr.success('Success!', 'Create Price List');
        this.priceListService.createSuccessEmitter.next(data);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Create Price List');
      }
    )
  }

}
