package pl.kostrzynski.twofactorauthentication.model;

import java.util.Arrays;
import java.util.Objects;

public class SecondAuth {

    private byte[] publicKeyBytes;
    private String EMAI;

    public SecondAuth(byte[] publicKeyBytes, String EMAI) {
        this.publicKeyBytes = publicKeyBytes;
        this.EMAI = EMAI;
    }

    public SecondAuth(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public void setPublicKeyBytes(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }

    public String getEMAI() {
        return EMAI;
    }

    public void setEMAI(String EMAI) {
        this.EMAI = EMAI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondAuth that = (SecondAuth) o;
        return Arrays.equals(publicKeyBytes, that.publicKeyBytes) && Objects.equals(EMAI, that.EMAI);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(EMAI);
        result = 31 * result + Arrays.hashCode(publicKeyBytes);
        return result;
    }

    @Override
    public String toString() {
        return "SecondAuth{" +
                "publicKeyBytes=" + Arrays.toString(publicKeyBytes) +
                ", EMAI='" + EMAI + '\'' +
                '}';
    }
}
