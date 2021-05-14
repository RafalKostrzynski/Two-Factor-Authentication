import { HttpClient, HttpHeaders, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthenticationResponse } from '../models/authentication-response';
import { QrCode } from '../models/qr-code';
import { User } from '../models/user';
import { baseURL } from '../shared/base-url';
import { ProcessHTTPMsgService } from './process-httpmsg-service.service';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  constructor(private http: HttpClient,
    private processHTTPMsgService: ProcessHTTPMsgService,
    private tokenStorageService: TokenStorageService) { }

  generateNewKey(): Observable<QrCode> {
    const httpOptions = {
      headers: this._createHeadersWithJWT()
    };
    return this.http.get<QrCode>(baseURL + 'for-user/pub-key/new-key-gen', httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  verificationMail(email: string): Observable<HttpStatusCode> {
    const httpOptions = {
      headers: this._createHeadersWithoutJWT()
    };
    return this.http.patch<HttpStatusCode>(baseURL + 'first-auth/verification-mail?email=' + email, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  forgotPassword(email: string): Observable<HttpStatusCode> {
    const httpOptions = {
      headers: this._createHeadersWithoutJWT()
    };
    return this.http.post<HttpStatusCode>(baseURL + 'first-auth/forgot-password?email=' + email, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  register(user: User): Observable<HttpStatusCode> {
    const httpOptions = {
      headers: this._createHeadersWithoutJWT()
    };
    return this.http.post<HttpStatusCode>(baseURL + 'first-auth/user', user, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  signIn(user: User): Observable<AuthenticationResponse> {
    const httpOptions = {
      headers: this._createHeadersWithoutJWT()
    };
    return this.http.post<AuthenticationResponse>(baseURL + 'first-auth/sign-in', user, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  authenticate(): Observable<AuthenticationResponse> {
    const httpOptions = {
      headers: this._createHeadersWithJWT()
    };
    return this.http.post<AuthenticationResponse>(baseURL + 'second-auth/authenticate', httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  updateUser(user: User): Observable<User> {
    const httpOptions = {
      headers: this._createHeadersWithJWT()
    };
    return this.http.put<User>(baseURL + 'for-user/user', user, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  private _createHeadersWithJWT(): HttpHeaders {
    var token = this.tokenStorageService.getToken();
    if (token != "")
      return new HttpHeaders({
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      })
    throw Error("Can't find token");
  }

  private _createHeadersWithoutJWT(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
}
