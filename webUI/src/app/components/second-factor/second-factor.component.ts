import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { QrCode } from 'src/app/models/qr-code';
import { HttpService } from 'src/app/services/http.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

@Component({
  selector: 'app-second-factor',
  templateUrl: './second-factor.component.html',
  styleUrls: ['./second-factor.component.scss']
})
export class SecondFactorComponent implements OnInit {

  authenticateQR: string = "";
  expirationTime?: number;
  expirationTimeSeconds?: number;
  errorMessage: string = "";
  requestFinished: boolean = false;
  interval: any;

  constructor(private httpService: HttpService,
    private tokenStorageService: TokenStorageService,
    private router: Router) { }

  ngOnInit(): void {
    var qrCodeObject: QrCode = history.state.data;
    this.authenticateQR = JSON.stringify(qrCodeObject);
    var expirationTimeFromQrCode = Math.floor((new Date(qrCodeObject.expirationTime).getTime() - new Date().getTime()) / 1000);
    this.requestAuthentication(expirationTimeFromQrCode);
    this.startCountDown(expirationTimeFromQrCode);
  }

  private async requestAuthentication(seconds: number) {
    await this.delay(5000);
    var amountOfCalls: number = seconds < 5 ? 1 : Math.floor(seconds / 5);
    for (let i = 0; i < amountOfCalls; i++) {
      if (!this.requestFinished) {
        this.createAuthenticationCall();
        await this.delay(5000);
      }
    }
  }

  private delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private startCountDown(seconds: number) {
    var decrementValue = 100 / 60;
    this.interval = setInterval(() => {
      this.expirationTime = seconds * decrementValue;
      this.expirationTimeSeconds = seconds;
      seconds--;
      if (seconds < 0) {
        clearInterval(this.interval);
        if (!this.requestFinished) {
          try {
            this.createAuthenticationCall()
          } finally { this.router.navigate(["/sign-in"]) }
        }
      }
    }, 1000);
  }

  private createAuthenticationCall(): boolean {
    this.httpService.authenticate().subscribe(
      data => {
        this.tokenStorageService.saveToken(data.jwtTokenWeb, data.expirationTime);
        // this.requestFinished = true;
        this.router.navigate(["/home"]);
      }, errorMessage => {
        if (errorMessage === "Access forbidden") this.errorMessage = "";
        else this.errorMessage = <any>errorMessage;
      })
    return false;
  }

  ngOnDestroy() {
    this.requestFinished = true;
    clearInterval(this.interval);
  }
}
