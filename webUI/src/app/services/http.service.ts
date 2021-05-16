import { HttpClient, HttpHeaders, HttpStatusCode } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
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

  generateNewKey(): Observable<QrCode> {
    return this.http.get<QrCode>(baseURL + 'for-user/pub-key/new-key-gen')
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  verificationMail(email: string): Observable<HttpStatusCode> {
    return this.http.patch<HttpStatusCode>(baseURL + 'first-auth/verification-mail?email=' + email,'')
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  resetPassword(token: string):Observable<QrCode>{
    return this.http.get<QrCode>(baseURL + 'first-auth/reset-password/' + token)
    .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  verifyEmail(token: string): Observable<any> {
    return this.http.get(baseURL + 'first-auth/verify-email/' + token, { responseType: 'text' })
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  forgotPassword(email: string): Observable<HttpStatusCode> {
    return this.http.post<HttpStatusCode>(baseURL + 'first-auth/forgot-password?email=' + email,'')
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  register(user: User): Observable<HttpStatusCode> {
    return this.http.post<HttpStatusCode>(baseURL + 'first-auth/user', user)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  signIn(user: User): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(baseURL + 'first-auth/sign-in', user)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  authenticate(): Observable<AuthenticationResponse> {
    return this.http.post<AuthenticationResponse>(baseURL + 'second-auth/authenticate','')
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  updateUser(user: User): Observable<User> {
    return this.http.put<User>(baseURL + 'for-user/user', user)
      .pipe(catchError(this.processHTTPMsgService.handleError));
  }

  getUser() : Observable<User> {
    return this.http.get<User>(baseURL + 'for-user/user')
    .pipe(catchError(this.processHTTPMsgService.handleError));
  }

}
