import { HttpStatusCode } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-after-registration',
  templateUrl: './after-registration.component.html',
  styleUrls: ['./after-registration.component.scss']
})
export class AfterRegistrationComponent {
  @ViewChild('fform') emailFormDirective: any;

  errorMessage: string = "";
  emailForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")])
  });

  constructor(private httpService: HttpService, private snackBar: MatSnackBar) { }

  resendMail(): void {
    var control = this.emailForm.get('email');
    if (control?.valid) {
      this.httpService.verificationMail(control?.value).subscribe(() => {
        this.snackBar.open("Email send successfully!", "Close");
      }, errorMessage => {
        this.errorMessage = <any>errorMessage
      });
    } else {
      this.errorMessage = "Please insert a valid email address into the email field";
    }
  }

  getErrorMessage(): string {
    var control = this.emailForm.get("email");
    if (control?.hasError("pattern")) return "Please enter a valid email address."
    return "Email is required.";
  }
}