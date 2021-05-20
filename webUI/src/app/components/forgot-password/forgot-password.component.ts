import { Component} from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent{
  errorMessage: string = "";
  emailForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,20}$")])
  });
  
  constructor(private httpService: HttpService, private snackBar: MatSnackBar) { }

  resendMail(): void {
    var control = this.emailForm.get('email');
    if (control?.valid) {
      this.httpService.forgotPassword(control?.value).subscribe(() => {
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
