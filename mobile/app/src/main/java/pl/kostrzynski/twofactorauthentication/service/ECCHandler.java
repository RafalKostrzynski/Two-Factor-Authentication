package pl.kostrzynski.twofactorauthentication.service;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class ECCHandler {

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp521r1"));
        return generator.generateKeyPair();
    }

    public byte[] getEncodedPublicKey(ECPublicKey publicKey){
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        return x509EncodedKeySpec.getEncoded();
    }

    public byte[] getEncodedPrivateKey(ECPrivateKey privateKey){
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        return pkcs8EncodedKeySpec.getEncoded();
    }

    public ECPrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(ks);
    }

    // TODO implement receiving public key from private key
    public ECPublicKey getPublicKeyFromPrivateKey(ECPrivateKey privateKey){
        return null;
    }

}
