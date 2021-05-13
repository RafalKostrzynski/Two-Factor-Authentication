import { HttpClient, HttpHeaders, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { AuthenticationResponse } from '../models/authentication-response';
import { QrCode } from '../models/qr-code';
import { User } from '../models/user';
import { baseURL } from '../shared/base-url';
import { ProcessHTTPMsgService } from './process-httpmsg-service.service';

@Injectable({
  providedIn: 'root'
})
export class HttpService {

  constructor(private http: HttpClient,
    private processHTTPMsgService: ProcessHTTPMsgService) { }

    public generateNewKey(): Observable<QrCode> {
      const httpOptions = {
        headers: this._createHeadersWithJWT()
      };
      return this.http.get<QrCode>(baseURL + 'for-user/pub-key/new-key-gen', httpOptions)
        .pipe(catchError(this.processHTTPMsgService.handleError));
    }

    public verificationMail(email: string): Observable<HttpStatusCode> {
      const httpOptions = {
        headers: this._createHeadersWithoutJWT()
      };
      return this.http.patch<HttpStatusCode>(baseURL + 'first-auth/verification-mail?email=' + email, httpOptions)
        .pipe(catchError(this.processHTTPMsgService.handleError));
    }

    public forgotPassword(email: string): Observable<HttpStatusCode> {
      const httpOptions = {
        headers: this._createHeadersWithoutJWT()
      };
      return this.http.post<HttpStatusCode>(baseURL + 'first-auth/forgot-password?email=' + email, httpOptions)
        .pipe(catchError(this.processHTTPMsgService.handleError));
    }

  public register(user: User): Observable<HttpStatusCode> {
    const httpOptions = {
      headers: this._createHeadersWithoutJWT()
    };
    return this.http.post<HttpStatusCode>(baseURL + 'first-auth/user', user, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  public signIn(user: User): Observable<AuthenticationResponse> {
    const httpOptions = {
      headers: this._createHeadersWithoutJWT()
    };
    return this.http.post<AuthenticationResponse>(baseURL + 'first-auth/sign-in', user, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  public authenticate(): Observable<AuthenticationResponse> {
    const httpOptions = {
      headers: this._createHeadersWithJWT()
    };
    return this.http.post<AuthenticationResponse>(baseURL + 'second-auth/authenticate', httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  public updateUser(user: User): Observable<User> {
    const httpOptions = {
      headers: this._createHeadersWithJWT()
    };
    return this.http.put<User>(baseURL + 'for-user/user', user, httpOptions)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  _createHeadersWithJWT(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
      //TODO add jwt 
    })
  }
  _createHeadersWithoutJWT(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
}
