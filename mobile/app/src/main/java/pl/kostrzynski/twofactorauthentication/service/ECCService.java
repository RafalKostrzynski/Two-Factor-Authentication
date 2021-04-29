package pl.kostrzynski.twofactorauthentication.service;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;


public class ECCService {
    private final String KEYSTORE_ALIAS = "EC_KEY_STORE";

    public KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
//        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
//        generator.initialize(new ECGenParameterSpec("secp256r1"));
//       // return generator.generateKeyPair();
        KeyPairGenerator kpg;
        kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        kpg.initialize(new KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA512)
                .build());
        KeyPair kp = kpg.generateKeyPair();
        return kp;

    }

    public byte[] getEncodedPrivateKey(ECPrivateKey privateKey) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        return pkcs8EncodedKeySpec.getEncoded();
    }

    public ECPrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPrivateKey) kf.generatePrivate(ks);
    }

    public String signMessage(String message) throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, UnrecoverableEntryException, InvalidKeyException, SignatureException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(KEYSTORE_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new IOException("Unexpected error please try again later");
        }
        Signature s = Signature.getInstance("SHA512withECDSA");
        s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
        s.update(message.getBytes());
        return Arrays.toString(s.sign());
    }
}
