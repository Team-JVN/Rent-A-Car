
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AuthentificationService } from 'src/app/service/authentification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PasswordMatch } from 'src/app/validator/passwordMatch';
import { Router } from '@angular/router';
import { RegistrationClient } from 'src/app/model/registrationClient';

@Component({
  selector: 'app-client-registration',
  templateUrl: './client-registration.component.html',
  styleUrls: ['./client-registration.component.css']
})
export class ClientRegistrationComponent implements OnInit {
  registrationForm: FormGroup;

  constructor(private toastr: ToastrService, private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder, private router: Router) { }

  ngOnInit() {
    this.registrationForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
      password: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$')]),
      repeatedPassword: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$')]),
      firstName: new FormControl(null, Validators.required),
      lastName: new FormControl(null, Validators.required),
      address: new FormControl(null, Validators.required),
      phoneNumber: new FormControl(null, [Validators.required, Validators.minLength(9), Validators.maxLength(10), Validators.pattern("0[0-9]+")]),
    }, {
      validator: PasswordMatch('password', 'repeatedPassword')
    })
  }

  register() {
    if (this.registrationForm.invalid) {
      this.toastr.error("Please enter valid data", 'Registration');
      return;
    }

    const name = this.registrationForm.value.firstName + '|' + this.registrationForm.value.lastName;
    const client = new RegistrationClient(name, this.registrationForm.value.email, this.registrationForm.value.password, this.registrationForm.value.address,
      this.registrationForm.value.phoneNumber)
    this.authentificationService.register(client).subscribe(
      () => {
        this.registrationForm.reset();
        this.toastr.success('Success!', 'Registration');
        this.router.navigate(['/client/pending-approval']);
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Registration');
      }
    );
  }
}
