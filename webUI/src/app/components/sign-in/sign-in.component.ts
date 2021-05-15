import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { HttpService } from 'src/app/services/http.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

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

  errorMessage: string = '';
  user!: User;
  hide = true;
  constructor(private httpService: HttpService,
    private tokenStorageService: TokenStorageService,
    private router: Router) { }

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
    if (control?.pristine) return '';
    controlName = controlName.charAt(0).toUpperCase() + controlName.slice(1);
    var errorMessage = '';
    if (control?.hasError("required")) errorMessage = `${controlName} is required.`;
    else if (control?.hasError('minlength')) errorMessage = `${controlName} must be at least ${minValue} characters long.`;
    else if (control?.hasError('maxlength')) errorMessage = `${controlName} can´t be longer than ${maxValue} characters.`;
    else if (control?.hasError('pattern')) errorMessage = `${controlName} must contain at least 1 uppercase, 1 special character (@#$%^&+'()=), 1 digit and must not contain white spaces`

    return errorMessage;
  }

  onSubmit() {
    var user: User = this.signInForm.value as User;
    if (this.signInForm.valid) {
      this.httpService.signIn(user).subscribe(
        data => {
          if (new Date(data.expirationTime).getTime() > new Date().getTime()) {
            this.tokenStorageService.saveToken(data.jwtTokenWeb, data.expirationTime);
            this.router.navigate(["/second-factor"]);
          }
        }, errorMessage => {
          if (errorMessage === "Access forbidden") this.errorMessage = "Username or password are incorrect, please try again"
          else this.errorMessage = <any>errorMessage
        })
    } else this.errorMessage = "Please validate your input data";
  }
}
