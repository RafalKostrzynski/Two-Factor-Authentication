import { Component } from '@angular/core';
import { QrCode } from 'src/app/models/qr-code';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-settings-key',
  templateUrl: './settings-key.component.html',
  styleUrls: ['./settings-key.component.scss']
})
export class SettingsKeyComponent {

  constructor(private httpService: HttpService) { }

  isQRRequested: boolean = false;
  generateKeyQR?: string;
  errorMessage?: string;
  interval: any;
  expirationTime?: number;
  expirationTimeSeconds?: number;

  changeView() {
    this.isQRRequested = !this.isQRRequested;
    this.httpService.generateNewKey().subscribe((data: QrCode) => {
      var expirationTimeFromQrCode = Math.floor((new Date(data.expirationTime).getTime() - new Date().getTime()) / 1000);
      this.startCountDown(expirationTimeFromQrCode);
      this.generateKeyQR = JSON.stringify(data)
    },
      errorMessage => this.errorMessage = errorMessage)
  }

  private startCountDown(seconds: number) {
    var decrementValue = 100 / 60;
    this.interval = setInterval(() => {
      this.expirationTime = seconds * decrementValue;
      this.expirationTimeSeconds = seconds;
      seconds--;
      if (seconds < 0) {
        clearInterval(this.interval);
        this.isQRRequested = false;
      }
    }, 1000);
  }

  ngOnDestroy() {
    this.isQRRequested = false;
    clearInterval(this.interval);
  }
}
