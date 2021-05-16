import { Component} from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from 'src/app/services/token-storage.service';

@Component({
  selector: 'app-header-authenticated',
  templateUrl: './header-authenticated.component.html',
  styleUrls: ['./header-authenticated.component.scss']
})
export class HeaderAuthenticatedComponent {

  constructor(private tokenStorageService:TokenStorageService, private router:Router) { }

  signOut(){
    this.tokenStorageService.signOut();
    this.router.navigate(['sign-in']);
  }

}
