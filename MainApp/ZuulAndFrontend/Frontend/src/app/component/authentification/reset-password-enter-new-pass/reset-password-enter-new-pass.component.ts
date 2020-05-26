import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AuthentificationService } from 'src/app/service/authentification.service';
import { HttpErrorResponse } from '@angular/common/http';
import { PasswordMatch } from 'src/app/validator/passwordMatch';

@Component({
  selector: 'app-reset-password-enter-new-pass',
  templateUrl: './reset-password-enter-new-pass.component.html',
  styleUrls: ['./reset-password-enter-new-pass.component.css']
})
export class ResetPasswordEnterNewPassComponent implements OnInit {

  resetPasswordForm: FormGroup;
  newHide = true;
  repeatedHide = true;
  token: string;

  constructor(private toastr: ToastrService, private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder, private route: ActivatedRoute) { }

  ngOnInit() {
    this.resetPasswordForm = this.formBuilder.group({
      newPassword: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$')]),
      repeatedPassword: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$')])
    }, {
      validator: PasswordMatch('newPassword', 'repeatedPassword')
    })
  }

  resetPassword() {
    this.route.queryParams.subscribe(params => {
      this.token = params.t;
    });
    this.authentificationService.resetPassword(this.token, this.resetPasswordForm.value.newPassword).subscribe(
      () => {
        this.resetPasswordForm.reset();
        this.toastr.success('Success!', 'Reset password');
        this.authentificationService.logout();
      },
      (httpErrorResponse: HttpErrorResponse) => {
        this.toastr.error(httpErrorResponse.error.message, 'Reset password');
      }
    );
  }

}
