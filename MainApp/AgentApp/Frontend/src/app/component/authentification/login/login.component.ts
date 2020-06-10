import { Router } from '@angular/router';
import { User } from './../../../model/user';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthentificationService } from 'src/app/service/authentification.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  hide = true;

  constructor(private toastr: ToastrService, private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder, private router: Router) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
      password: new FormControl(null, [Validators.required, Validators.minLength(10), Validators.maxLength(64), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[_#?!@$%^&*-.,:;]).{10,64}$')])
    })
  }

  login() {
    this.authentificationService.login(new User(this.loginForm.value.email, this.loginForm.value.password)).subscribe(
      () => {
        this.loginForm.reset();
        this.toastr.success('Success!', 'Login');
        this.redirectToHomePage();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        if (httpErrorResponse.status == 406) {
          this.toastr.info("You have to change received generic password on first attempt to login.", 'Login');
          this.router.navigate(['/change-password']);
        } else {
          this.toastr.error(httpErrorResponse.error.message, 'Login');
        }
      }
    );
  }

  redirectToHomePage() {
    if (this.authentificationService.isAgent()) {
      this.router.navigate(['/advertisements']);
    } else if (this.authentificationService.isClient()) {
      this.router.navigate(['/search-advertisements']);
    } else {
      this.authentificationService.logout();
    }
  }


}
