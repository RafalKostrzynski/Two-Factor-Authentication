import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss']
})
export class ResetPasswordComponent implements OnInit {

  constructor(private httpService: HttpService, private activatedRoute: ActivatedRoute) { }

  errorMessage?: string;
  generateKeyQR?: string;

  ngOnInit(): void {
    var token: string = this.activatedRoute.snapshot.paramMap.get("token")!;
    this.httpService.resetPassword(token).subscribe(
      response => this.generateKeyQR = JSON.stringify(response), 
      errorMessage => this.errorMessage = <any>errorMessage
    );
  }

}
