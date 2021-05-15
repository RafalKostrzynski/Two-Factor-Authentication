import { Injectable } from '@angular/core';
import { throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from 'src/app/models/errorResponse';


@Injectable({
  providedIn: 'root'
})
export class ProcessHTTPMsgService {

  constructor() { }

  public handleError(error: HttpErrorResponse | any) {
    let errMsg: string;

    if (error.error instanceof ErrorEvent) {
      errMsg = error.error.message;
    }
    else {
      var errorResponse = error.error as ErrorResponse;
      if (errorResponse.errors?.toString() !== undefined)
        errMsg = `${errorResponse.errors?.toString()}`;
      else if (error.error.status===403)
        errMsg = `Access forbidden`;
      else errMsg = "Something went wrong please try again later";
    }
    return throwError(errMsg);
  }

}