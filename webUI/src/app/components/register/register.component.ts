import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { HttpService } from 'src/app/services/http.service';
import { validatePasswords } from 'src/app/shared/validate-password';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  @ViewChild('fform') registerFormDirective: any;

  registerForm = new FormGroup({
    username: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(30)]),
    email: new FormControl('', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,20}$")]),
    password: new FormControl('', [Validators.required, Validators.minLength(9), Validators.maxLength(60),
    Validators.pattern("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+()'=])(?=\\S+$).{9,60}")]),
    repeatPassword: new FormControl()
  }, { validators: validatePasswords })
  hide = true;
  errorMessage?: string;

  constructor(private httpService: HttpService,
    private router: Router) { }

  ngOnInit(): void {
  }

  getUsernameErrorMessage(): string {
    return this.getErrorMessage("username", "5", "30");
  }

  getPasswordErrorMessage(): string {
    return this.getErrorMessage("password", "9", "60");
  }

  getEmailErrorMessage(): string {
    var control = this.registerForm.get("email");
    if (control?.hasError("pattern")) return "Please enter a valid email address."
    return "Email is required.";
  }

  getErrorMessage(controlName: string, minValue: string, maxValue: string): string {
    var control = this.registerForm.get(controlName);
    controlName = controlName.charAt(0).toUpperCase() + controlName.slice(1);
    var errorMessage = '';
    if (control?.hasError("required")) errorMessage = `${controlName} is required.`;
    else if (control?.hasError('minlength')) errorMessage = `${controlName} must be at least ${minValue} characters long.`;
    else if (control?.hasError('maxlength')) errorMessage = `${controlName} can´t be longer than ${maxValue} characters.`;
    else if (control?.hasError('pattern')) errorMessage = `${controlName} must contain at least 1 uppercase, 1 special character (@#$%^&+'()=), 1 digit and must not contain white spaces.`

    return errorMessage;
  }

  onSubmit() {
    var user: User = this.registerForm.value as User;
    this.httpService.register(user).subscribe(() => {
      this.router.navigate(["/after-registration"]);
    }, errorMessage => {
      this.errorMessage = errorMessage === "Wrong input, try again later" ?
       "Email or username already taken" :
        <any>errorMessage;
    })
  }
}