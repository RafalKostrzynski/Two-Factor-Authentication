import { ThisReceiver } from '@angular/compiler';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { User } from 'src/app/models/user';
import { HttpService } from 'src/app/services/http.service';
import { validatePasswords } from 'src/app/shared/validate-password';

@Component({
  selector: 'app-settings-user',
  templateUrl: './settings-user.component.html',
  styleUrls: ['./settings-user.component.scss']
})
export class SettingsUserComponent implements OnInit {
  @ViewChild('fform') registerFormDirective: any;

  changeUserForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(30)]),
    email: new FormControl('', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]),
    password: new FormControl('', [Validators.required, Validators.minLength(9), Validators.maxLength(60),
    Validators.pattern("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+()'=])(?=\\S+$).{9,60}")]),
    repeatPassword: new FormControl()
  }, { validators: validatePasswords })
  hide = true;
  errorMessage?: string;

  constructor(private httpService: HttpService, private snackbar: MatSnackBar) { }

  ngOnInit(): void {
  }

  getUsernameErrorMessage(): string {
    return this.getErrorMessage("username", "5", "30");
  }

  getPasswordErrorMessage(): string {
    return this.getErrorMessage("password", "9", "60");
  }

  getEmailErrorMessage(): string {
    var control = this.changeUserForm.get("email");
    if (control?.hasError("pattern")) return "Please enter a valid email address."
    return "Email is required.";
  }

  getErrorMessage(controlName: string, minValue: string, maxValue: string): string {
    var control = this.changeUserForm.get(controlName);
    if (control?.pristine) return '';
    controlName = controlName.charAt(0).toUpperCase() + controlName.slice(1);
    var errorMessage = '';
    if (control?.hasError("required")) errorMessage = `${controlName} is required.`;
    else if (control?.hasError('minlength')) errorMessage = `${controlName} must be at least ${minValue} characters long.`;
    else if (control?.hasError('maxlength')) errorMessage = `${controlName} can´t be longer than ${maxValue} characters.`;
    else if (control?.hasError('pattern')) errorMessage = `${controlName} must contain at least 1 uppercase, 1 special character (@#$%^&+'()=), 1 digit and must not contain white spaces.`

    return errorMessage;
  }

  onSubmit() {
    var user: User = this.changeUserForm.value as User;
    this.errorMessage = '';
    this.httpService.updateUser(user).subscribe((data: User) => {
      this.changeUserForm.setValue({ username: data.username, email: data.email, password: '******', repeatPassword: '******' });
      this.snackbar.open("User changed successfully!", "Close");
    }, errorMessage => {
      this.errorMessage = <any>errorMessage
    })
  }

}
