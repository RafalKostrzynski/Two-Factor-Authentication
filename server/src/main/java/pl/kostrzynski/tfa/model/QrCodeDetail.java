package pl.kostrzynski.tfa.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QrCodeDetail {

    private String Payload;
    private String jwtToken;
    private String expirationTime;

    // TODO maybe add token for CSRF
}
