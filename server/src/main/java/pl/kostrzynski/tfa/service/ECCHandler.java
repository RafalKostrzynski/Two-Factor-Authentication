package pl.kostrzynski.tfa.service;

import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class ECCHandler {

    public PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePublic(spec);
    }

    public String encodeContent(String content, PublicKey key) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] contentBytes = content.getBytes();
        Cipher cipher = Cipher.getInstance("EC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherContent = cipher.doFinal(contentBytes);
        return Base64.getEncoder().encodeToString(cipherContent);
    }

}
