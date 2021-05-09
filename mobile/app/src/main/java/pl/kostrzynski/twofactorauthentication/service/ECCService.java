package pl.kostrzynski.twofactorauthentication.service;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class ECCService {

    private final String KEYSTORE_ALIAS = "EC_KEY_STORE";

    public PublicKey generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        kpg.initialize(new KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA512)
                .build());
        KeyPair kp = kpg.generateKeyPair();
        return kp.getPublic();
    }

    public void deleteKey() throws KeyStoreException, CertificateException,
            IOException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        ks.deleteEntry(KEYSTORE_ALIAS);
    }

    public String signMessage(String message) throws KeyStoreException, CertificateException, NoSuchAlgorithmException,
            IOException, UnrecoverableEntryException, InvalidKeyException, SignatureException {

        KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
        ks.load(null);
        KeyStore.Entry entry = ks.getEntry(KEYSTORE_ALIAS, null);
        if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
            throw new KeyStoreException("Unexpected error please try again later");
        }

        Signature s = Signature.getInstance("SHA512withECDSA");
        s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
        s.update(message.getBytes());
        return Base64.encodeToString(s.sign(), Base64.NO_WRAP);
    }

    public boolean keyExists() {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(KEYSTORE_ALIAS, null);
            return entry instanceof KeyStore.PrivateKeyEntry;

        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException |
                KeyStoreException exception) {
            return false;
        }
    }
}
