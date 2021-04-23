package pl.kostrzynski.twofactorauthentication.model;

import java.util.Arrays;

public class SecondAuth {

    private byte[] publicKeyBytes;

    public SecondAuth(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public void setPublicKeyBytes(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondAuth that = (SecondAuth) o;
        return Arrays.equals(publicKeyBytes, that.publicKeyBytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(publicKeyBytes);
    }

    @Override
    public String toString() {
        return "SecondAuth{" +
                "publicKeyBytes=" + Arrays.toString(publicKeyBytes) +
                '}';
    }
}
