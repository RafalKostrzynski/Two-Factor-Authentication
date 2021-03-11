package pl.kostrzynski.tfa.exception;

public class ApiMethodException extends RuntimeException{

    private final ApiErrorCodeEnum apiErrorCodeEnum;

    public ApiMethodException(String message, ApiErrorCodeEnum apiErrorCodeEnum) {
        super(message);
        this.apiErrorCodeEnum = apiErrorCodeEnum;
    }

    public ApiErrorCodeEnum getApiErrorCodeEnum(){
        return apiErrorCodeEnum;
    }
}
