package pl.kostrzynski.tfa.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthenticationResponse {
    private String jwtToken;
    private String qrCode;
    private LocalDateTime expirationTime;

    public AuthenticationResponse(String jwtToken, String qrCode, long expirationTime) {
        this.jwtToken = jwtToken;
        this.qrCode = qrCode;
        this.expirationTime= LocalDateTime.now().plusSeconds(expirationTime);
    }
}
