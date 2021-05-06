package pl.kostrzynski.tfa.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.kostrzynski.tfa.model.enums.QrPurpose;

@Data
@AllArgsConstructor
public class QrCodeDetail {

    private QrPurpose purpose;
    private String Payload;
    private String jwtToken;
    private String expirationTime;

    // TODO maybe add token for CSRF
}
