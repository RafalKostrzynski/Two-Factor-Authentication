import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  constructor(private httpService: HttpService, private activatedRoute: ActivatedRoute, private router: Router) { }

  errorMessage?: string;
  generateKeyQR?: string;
  interval: any;
  expirationTime?: number;
  expirationTimeSeconds?: string;

  ngOnInit(): void {
    var token: string = this.activatedRoute.snapshot.paramMap.get("token")!;
    this.httpService.resetPassword(token).subscribe(
      response => {
        var expirationTimeFromQrCode = Math.floor((new Date(response.expirationTime).getTime() - new Date().getTime()) / 1000);
        this.startCountDown(expirationTimeFromQrCode);
        this.generateKeyQR = JSON.stringify(response)
      },
      errorMessage => this.errorMessage = <any>errorMessage
    );
  }

  private startCountDown(seconds: number) {
    var decrementValue = 100 / 900;
    this.interval = setInterval(() => {
      this.expirationTime = seconds * decrementValue;
      this.expirationTimeSeconds = this.getTimeString(seconds);
      seconds--;
      if (seconds < 0) {
        clearInterval(this.interval);
        this.router.navigate(["/sign-in"]);
      }
    }, 1000);
  }

  private getTimeString(seconds: number): string {
    return ('0' + Math.floor(seconds % 3600 / 60)).slice(-2) + ":" + ('0' + Math.floor(seconds % 3600 % 60)).slice(-2);
  }

  ngOnDestroy() {
    clearInterval(this.interval);
  }

}
