package pl.kostrzynski.tfa.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SessionInitializerObject {
    private String sessionID;
    private String encodedOTP;
    private LocalTime validationEnd;

    public SessionInitializerObject(String sessionID, String encodedOTP) {
        this.sessionID = sessionID;
        this.encodedOTP = encodedOTP;
    }

    public boolean isNotExpired(){
        return validationEnd.isBefore(LocalTime.now());
    }
}
