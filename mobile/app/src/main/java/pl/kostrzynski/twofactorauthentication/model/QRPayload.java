package pl.kostrzynski.twofactorauthentication.model;

import lombok.Data;
import lombok.NonNull;
import pl.kostrzynski.twofactorauthentication.model.enums.QrPurpose;

import java.time.LocalDateTime;

@Data
public class QRPayload {

    private QrPurpose purpose;
    private String payload;
    private String jwtToken;
    private LocalDateTime expirationTime;

    public QRPayload(@NonNull QrPurpose purpose, String payload, @NonNull String jwtToken,
                     @NonNull LocalDateTime expirationTime) {
        this.purpose = purpose;
        this.payload = payload;
        this.jwtToken = jwtToken;
        this.expirationTime = expirationTime;
    }
}
