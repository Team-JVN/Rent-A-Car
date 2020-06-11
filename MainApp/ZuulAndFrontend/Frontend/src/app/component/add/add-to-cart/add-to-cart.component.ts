import { SearchParamsForRentInfo } from './../../../model/searchParamsForRentInfo';
import { RentInfo } from './../../../model/rentInfo';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';

@Component({
  selector: 'app-add-to-cart',
  templateUrl: './add-to-cart.component.html',
  styleUrls: ['./add-to-cart.component.css']
})
export class AddToCartComponent implements OnInit {

  informationForm: FormGroup;

  constructor(
    private toastr: ToastrService,
    private dialogRef: MatDialogRef<AddToCartComponent>,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public searchParams: SearchParamsForRentInfo) { }

  ngOnInit() {
    this.informationForm = this.formBuilder.group({
      optedForCDW: new FormControl(null)
    });
  }

  addToCart() {
    this.searchParams.optedForCDW = this.informationForm.value.optedForCDW;
    const newRentInfo = new RentInfo(this.searchParams.dateTimeFrom, this.searchParams.dateTimeTo, this.searchParams.optedForCDW, this.searchParams.advertisement);

    let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    rentInfos.push(newRentInfo);
    localStorage.setItem("rentInfos", JSON.stringify(rentInfos));
    this.informationForm.reset();
    this.dialogRef.close();
    this.toastr.success('Success!', 'Add to Cart');
  }

}
