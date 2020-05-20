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

  constructor(private toastr: ToastrService, private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder, private router: Router) { }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
      password: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$')])
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
        this.toastr.error(httpErrorResponse.error.message, 'Login');
      }
    );
  }

  redirectToHomePage() {
    if (this.authentificationService.isAgent()) {
      console.log("AGENT HAJ")
      this.router.navigate(['']);
    } else if (this.authentificationService.isClient()) {
      this.router.navigate(['/search-advertisements']);
    } else if (this.authentificationService.isAdmin()) {
      this.router.navigate(['']);
    } else {
      this.authentificationService.logout();
    }
  }


}