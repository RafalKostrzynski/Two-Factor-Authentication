package pl.kostrzynski.tfa.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QrCodeDetail {

    private String Payload;
    private String jwtToken;
    private LocalDateTime expirationTime;

    // TODO maybe add token for CSRF
}
