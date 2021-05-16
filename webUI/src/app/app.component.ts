import { Component } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { filter } from 'rxjs/internal/operators/filter';
import { TokenStorageService } from './services/token-storage.service';
import { nonAuthenticatedRoutes } from './shared/non-authenticated-routes';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  authenticatedRoute: boolean = false;
  title = 'Two Factor Authentication';
  private nonAuthenticatedRoutes: string[] = nonAuthenticatedRoutes;

  constructor(router: Router, tokenStorage: TokenStorageService) {
    router.events.pipe(filter(event => event instanceof NavigationStart))
      .subscribe(event => {
        var eventNav = event as NavigationStart;
        this.authenticatedRoute = this.nonAuthenticatedRoutes.some(e => eventNav.url.includes(e));
        if (!this.authenticatedRoute && tokenStorage.getToken() === '')
          router.navigate(['/sign-in']);
      });
  }
}
