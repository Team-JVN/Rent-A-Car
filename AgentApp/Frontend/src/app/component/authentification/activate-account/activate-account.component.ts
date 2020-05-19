import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { ClientService } from './../../../service/client.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.css']
})
export class ActivateAccountComponent implements OnInit {

  success = false;
  errorMessage = "Unknown error occured.";

  constructor(
    private clientService: ClientService,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const token = params.t;
      this.activateAccount(token)
    });
  }

  activateAccount(token: string) {
    this.clientService.activateAccount(token).subscribe(
      () => {
        this.success = true;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.success = false;
        this.errorMessage = httpErrorResponse.error.message;
      }
    );
  }

}
