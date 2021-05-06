package pl.kostrzynski.twofactorauthentication.model;

import pl.kostrzynski.twofactorauthentication.model.enums.QrPurpose;

import java.time.LocalDateTime;
import java.util.Objects;

public class QRPayload {

    private QrPurpose purpose;
    private String payload;
    private String jwtToken;
    private LocalDateTime expirationTime;

    public QRPayload(QrPurpose purpose, String payload, String jwtToken, LocalDateTime expirationTime) {
        this.purpose = purpose;
        this.payload = payload;
        this.jwtToken = jwtToken;
        this.expirationTime = expirationTime;
    }

    public QrPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(QrPurpose purpose) {
        this.purpose = purpose;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRPayload qrPayload = (QRPayload) o;
        return purpose == qrPayload.purpose && Objects.equals(payload, qrPayload.payload) && Objects.equals(jwtToken, qrPayload.jwtToken) && Objects.equals(expirationTime, qrPayload.expirationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purpose, payload, jwtToken, expirationTime);
    }

    @Override
    public String toString() {
        return "QRPayload{" +
                "qrPurpose=" + purpose +
                ", payload='" + payload + '\'' +
                ", jwtToken='" + jwtToken + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
