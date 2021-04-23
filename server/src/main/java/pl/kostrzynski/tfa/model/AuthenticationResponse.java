package pl.kostrzynski.tfa.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthenticationResponse {
    private String jwtTokenWeb;
    private String qrCode;
    private LocalDateTime expirationTime;

    public AuthenticationResponse(String jwtTokenWeb, String qrCode, long expirationTime) {
        this.jwtTokenWeb = jwtTokenWeb;
        this.qrCode = qrCode;
        this.expirationTime = LocalDateTime.now().plusSeconds(expirationTime);
    }
}
