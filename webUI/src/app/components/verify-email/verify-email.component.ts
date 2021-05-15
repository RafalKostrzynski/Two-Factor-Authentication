import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-verify-email',
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss']
})
export class VerifyEmailComponent implements OnInit {

  constructor(private httpService: HttpService, private activatedRoute: ActivatedRoute) { }

  errorMessage?: string;
  generateKeyQR?: string;

  ngOnInit(): void {
    var token: string = this.activatedRoute.snapshot.paramMap.get("token")!;
    this.httpService.verifyEmail(token).subscribe(
      response => {
        console.log(response.toString());
        this.generateKeyQR = response;
      }, errorMessage => this.errorMessage = <any>errorMessage
    );
  }

}
