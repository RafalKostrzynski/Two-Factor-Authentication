package pl.kostrzynski.twofactorauthentication.model;

import java.util.Arrays;
import java.util.Objects;

public class SecondAuth {

    private byte[] publicKeyBytes;
    private String androidID;

    public SecondAuth(byte[] publicKeyBytes, String androidID) {
        this.publicKeyBytes = publicKeyBytes;
        this.androidID = androidID;
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

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondAuth that = (SecondAuth) o;
        return Arrays.equals(publicKeyBytes, that.publicKeyBytes) && Objects.equals(androidID, that.androidID);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(androidID);
        result = 31 * result + Arrays.hashCode(publicKeyBytes);
        return result;
    }

    @Override
    public String toString() {
        return "SecondAuth{" +
                "publicKeyBytes=" + Arrays.toString(publicKeyBytes) +
                ", androidID='" + androidID + '\'' +
                '}';
    }
}
