package pl.kostrzynski.tfa.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.mail.MessagingException;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler({MessagingException.class, IllegalArgumentException.class,
            SecurityException.class, ApiMethodException.class, DataIntegrityViolationException.class,
            JsonProcessingException.class})
    public final ResponseEntity<ApiError> exceptionHandler(Exception exception) {
        HttpStatus httpStatus;
        String error;
        if (exception instanceof ApiMethodException) {
            return createResponseEntityForApiMethodException((ApiMethodException) exception);
        } else if (exception instanceof MessagingException || exception instanceof JsonProcessingException) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Couldn't send requested Email";
        } else if (exception instanceof IllegalArgumentException
                || exception instanceof DataIntegrityViolationException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            error = "Wrong input, try again later";
        } else if (exception instanceof SecurityException) {
            httpStatus = HttpStatus.BAD_REQUEST;
            error = "Wrong input, try again later";
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            error = "Unknown error occurred please try again later";
        }
        String message = secureMessageOutput(exception.getLocalizedMessage());

        return new ResponseEntity<>(new ApiError(httpStatus, message, error), httpStatus);
    }

    private ResponseEntity<ApiError> createResponseEntityForApiMethodException(ApiMethodException exception) {
        HttpStatus httpStatus;
        String message;

        switch (exception.getApiErrorCodeEnum()) {
            case NOT_ACCEPTABLE:
                httpStatus = HttpStatus.NOT_ACCEPTABLE;
                message = "Could not accept provided data, please check input";
                break;
            case NOT_FOUND:
                httpStatus = HttpStatus.NOT_FOUND;
                message = "Requested data not found";
                break;
            case FORBIDDEN:
                httpStatus = HttpStatus.FORBIDDEN;
                message = "Can't execute this operation";
                break;
            default: {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                message = "Unknown error occurred";
            }
        }
        return new ResponseEntity<>(
                new ApiError(httpStatus, message, exception.getLocalizedMessage()),
                httpStatus);
    }

    private String secureMessageOutput(String message) {
        return message.contains("pl.kostrzynski.tfa.model.") ?
                message.replace("pl.kostrzynski.tfa.model.", "") : message;
    }

}
