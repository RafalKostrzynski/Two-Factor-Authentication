package pl.kostrzynski.twofactorauthentication.model;

import java.util.Objects;

public class QRPayload {

    private String Payload;
    private String jwtToken;

    public QRPayload(String payload, String jwtToken) {
        Payload = payload;
        this.jwtToken = jwtToken;
    }

    public String getPayload() {
        return Payload;
    }

    public void setPayload(String payload) {
        Payload = payload;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRPayload qrPayload = (QRPayload) o;
        return Payload.equals(qrPayload.Payload) && jwtToken.equals(qrPayload.jwtToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Payload, jwtToken);
    }

    @Override
    public String toString() {
        return "QRPayload{" +
                "Payload='" + Payload + '\'' +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }
}
