package pl.kostrzynski.tfa.service;

import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.entity.Payload;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class ECCHandler {

    private PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC");
            return kf.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new SecurityException("Provided key is not valid!");
        }
    }

//    public String encodeContent(String content, PublicKey key) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        byte[] contentBytes = content.getBytes();
//        Cipher cipher = Cipher.getInstance("EC");
//        cipher.init(Cipher.ENCRYPT_MODE, key);
//        byte[] cipherContent = cipher.doFinal(contentBytes);
//        return Base64.getEncoder().encodeToString(cipherContent);
//    }

    public boolean isValidSignature(String signature, SmartphoneDetails smartphoneDetails,
                                    SecondAuth secondAuth, Payload payload) {
        PublicKey publicKey = getPublicKeyFromBytes(secondAuth.getPublicKeyBytes());
        try {
            Signature ecdsaVerify = Signature.getInstance("secp256r1");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(getMessage(smartphoneDetails, payload).getBytes(StandardCharsets.UTF_8));
            return ecdsaVerify.verify(Base64.getDecoder().decode(signature));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new SecurityException("Something went wrong please try again later");
        }
    }

    private String getMessage(SmartphoneDetails smartphoneDetails, Payload payload) {
        return payload.getValue()+smartphoneDetails.getSmartphoneDetails();
    }

    public boolean isValidPublicKey(byte[] publicKey) {
        getPublicKeyFromBytes(publicKey);
        return true;
    }

}
