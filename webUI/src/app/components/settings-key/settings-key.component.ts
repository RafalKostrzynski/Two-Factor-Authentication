import { Component, OnInit } from '@angular/core';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-settings-key',
  templateUrl: './settings-key.component.html',
  styleUrls: ['./settings-key.component.scss']
})
export class SettingsKeyComponent {

  constructor(private httpService: HttpService) { }

  isQRRequested: boolean = false;

  changeView() {
    this.isQRRequested = !this.isQRRequested;
  }

}
