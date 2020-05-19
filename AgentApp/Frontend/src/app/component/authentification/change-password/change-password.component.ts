import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { AuthentificationService } from 'src/app/service/authentification.service';
import { ChangePassword } from 'src/app/model/changePassword';
import { HttpErrorResponse } from '@angular/common/http';
import { PasswordMatch } from 'src/app/validator/passwordMatch';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  changePasswordForm: FormGroup;
  hide = true;
  newHide = true;
  repeatedHide = true;

  constructor(private toastr: ToastrService, private authentificationService: AuthentificationService,
    private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.changePasswordForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.email]),
      oldPassword: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$')]),
      newPassword: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$')]),
      repeatedPassword: new FormControl(null, [Validators.required, Validators.minLength(8), Validators.maxLength(64), Validators.pattern('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,64}$')])
    }, {
      validator: PasswordMatch('newPassword', 'repeatedPassword')
    })
  }

  changePassword() {
    this.authentificationService.changePassword(new ChangePassword(this.changePasswordForm.value.email, this.changePasswordForm.value.oldPassword,
      this.changePasswordForm.value.newPassword)).subscribe(
        () => {
          this.changePasswordForm.reset();
          this.toastr.success('Success!', 'Change password');
          this.authentificationService.logout();
        },
        (httpErrorResponse: HttpErrorResponse) => {
          this.toastr.error(httpErrorResponse.error.message, 'Change password');
        }
      );
  }

}
