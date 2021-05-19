import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { HttpService } from 'src/app/services/http.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(private httpService: HttpService, private router:Router, private snackBar:MatSnackBar) { }

  user?: User;

  ngOnInit(): void {
    this.httpService.getUser().subscribe(data => this.user = data,
      errorMessage => {
        (errorMessage as string).includes("Access forbidden")?
         this.router.navigate(['/sign-in']):
         this.snackBar.open("It's seems that we have an internal problem, please connect again later", "close");
      }
      );
  }

}
