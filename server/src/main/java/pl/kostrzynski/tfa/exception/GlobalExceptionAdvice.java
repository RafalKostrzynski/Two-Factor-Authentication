package pl.kostrzynski.tfa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.mail.MessagingException;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({MessagingException.class, IllegalArgumentException.class})
    public final ResponseEntity<Object> exceptionHandler(Exception exception){
        HttpStatus httpStatus;
        String error;
        if(exception instanceof MessagingException) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Couldn't send requested Email";
        }else if(exception instanceof IllegalArgumentException){
            httpStatus = HttpStatus.BAD_REQUEST;
            error = "Wrong input, try again later";
        }else {
            httpStatus=HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Unknown error occurred please try again later";
        }
        String message = secureMessageOutput(exception.getLocalizedMessage());

        return new ResponseEntity<>(new ApiError(httpStatus,message,error), httpStatus);
    }

    private String secureMessageOutput(String message){
        return message.contains("pl.kostrzynski.tfa.model.")?
                message.replace("pl.kostrzynski.tfa.model.",""):message;
    }

}
