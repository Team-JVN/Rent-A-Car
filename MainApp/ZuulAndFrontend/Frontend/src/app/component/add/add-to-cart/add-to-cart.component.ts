import { SearchParamsForRentInfo } from './../../../model/searchParamsForRentInfo';
import { AuthentificationService } from './../../../service/authentification.service';
import { RentInfo } from './../../../model/rentInfo';
import { RentRequest } from './../../../model/rentRequest';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClientService } from './../../../service/client.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { Component, OnInit, Inject } from '@angular/core';
import { RentRequestService } from 'src/app/service/rent-request.service';

@Component({
  selector: 'app-add-to-cart',
  templateUrl: './add-to-cart.component.html',
  styleUrls: ['./add-to-cart.component.css']
})
export class AddToCartComponent implements OnInit {

  informationForm: FormGroup;

  constructor(
    private toastr: ToastrService,
    private rentRequestService: RentRequestService,
    private authService: AuthentificationService,
    private dialogRef: MatDialogRef<AddToCartComponent>,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public searchParams: SearchParamsForRentInfo) { }

  ngOnInit() {
    this.informationForm = this.formBuilder.group({
      optedForCDW: new FormControl(null)
    });
  }

  create() {
    this.searchParams.optedForCDW = this.informationForm.value.optedForCDW;

    console.log("Ima CDW!");
    console.log(this.searchParams);

    //   const newRentInfo = new RentInfo(dateTimeFrom, dateTimeTo, cdw, this.selectedItem.id);

    //   if (this.selectedItem.owner.email === this.authService.getLoggedInUserEmail()) {
    //     var rentInfos = [];
    //     rentInfos.push(newRentInfo);
    //     const rentRequest = new RentRequest(this.clientForm.value.client.id, rentInfos);
    //     this.rentRequestService.create(rentRequest).subscribe(
    //       (data: RentRequest) => {
    //         this.clientForm.reset();
    //         this.informationForm.reset();
    //         this.dialogRef.close();
    //         this.toastr.success('Success.', 'Create Rent Request');
    //         this.rentRequestService.createSuccessEmitter.next(data);
    //       },
    //       (httpErrorResponse: HttpErrorResponse) => {
    //         this.toastr.error(httpErrorResponse.error.message, 'Create Rent Request');
    //       }
    //     );
    //   } else {
    //     let rentInfos: RentInfo[] = JSON.parse(localStorage.getItem("rentInfos") || "[]");
    //     rentInfos.push(newRentInfo);
    //     localStorage.setItem("rentInfos", JSON.stringify(rentInfos));
    //     this.informationForm.reset();
    //     this.dialogRef.close();
    //     this.toastr.success('Successfully added to cart!', 'Create Rent Request');
    //   }
  }

}
