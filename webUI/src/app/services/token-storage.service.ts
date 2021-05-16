import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  signOut(): void {
    localStorage.removeItem("JWT_TOKEN");
    localStorage.removeItem("EXPIRATION_TIME");
  }

  saveToken(token: string, expirationTimeDate: Date) {
    localStorage.setItem("EXPIRATION_TIME", expirationTimeDate.toString())
    localStorage.setItem("JWT_TOKEN", token);
  }

  getToken(): string {
    var token = localStorage.getItem("JWT_TOKEN");
    var expirationTimeString = localStorage.getItem("EXPIRATION_TIME");
    if (token != null && expirationTimeString != null) {
      if (new Date(expirationTimeString).getTime() > new Date().getTime()) {
        return token;
      }
    }
    return "";
  }

}
