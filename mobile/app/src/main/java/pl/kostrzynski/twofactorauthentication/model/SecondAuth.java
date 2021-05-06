package pl.kostrzynski.twofactorauthentication.model;

import lombok.Data;

@Data
public class SecondAuth {

    private byte[] publicKeyBytes;

    public SecondAuth(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }
}
