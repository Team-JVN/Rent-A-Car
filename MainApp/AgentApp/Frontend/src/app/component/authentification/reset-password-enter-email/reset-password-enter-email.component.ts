import { Router } from '@angular/router';
import { User } from './../../../model/user';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthentificationService } from 'src/app/service/authentification.service';

@Component({
  selector: 'app-reset-password-enter-email',
  templateUrl: './reset-password-enter-email.component.html',
  styleUrls: ['./reset-password-enter-email.component.css']
})
export class ResetPasswordEnterEmailComponent implements OnInit {

  emailForm: FormGroup;
  confirmed = false;
  email: string;

  constructor(private toastr: ToastrService, private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder, private router: Router) { }

  ngOnInit() {
    this.emailForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
    })
  }

  requestToken() {
    this.authentificationService.requestToken(this.emailForm.value.email).subscribe(
      () => {
        this.email = this.emailForm.value.email;
        this.emailForm.reset();
        this.confirmed = true;
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Reset password');
        this.confirmed = false;
      }
    );
  }

}
