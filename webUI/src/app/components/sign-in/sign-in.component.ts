import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/models/user';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements OnInit {
  @ViewChild('fform') signInFormDirective: any;

  signInForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(30)]),
    password: new FormControl('', [Validators.required, Validators.minLength(9), Validators.maxLength(60), 
      Validators.pattern("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+()'=])(?=\\S+$).{9,60}")]),
  })
  
  user!: User;
  hide = true;
  constructor() { }

  ngOnInit(): void {
  }

  getUsernameErrorMessage(): string {
    return this.getErrorMessage("username", "5", "30");
  }

  getPasswordErrorMessage(): string {
    return this.getErrorMessage("password", "9", "60");
  }

  getErrorMessage(controlName: string, minValue: string, maxValue: string): string {
    var control = this.signInForm.get(controlName);
    if(control?.pristine) return '';
    controlName = controlName.charAt(0).toUpperCase() + controlName.slice(1);
    var errorMessage = '';
    if(control?.hasError("required")) errorMessage = `${controlName} is required.`;
    else if (control?.hasError('minlength'))errorMessage = `${controlName} must be at least ${minValue} characters long.`;
    else if (control?.hasError('maxlength'))errorMessage = `${controlName} can´t be longer than ${maxValue} characters.`;
    else if (control?.hasError('pattern'))errorMessage = `${controlName} must contain at least 1 uppercase, 1 special character (@#$%^&+'()=), 1 digit and must not contain white spaces`

    return errorMessage;
  }

  onSubmit() {

  }

}
