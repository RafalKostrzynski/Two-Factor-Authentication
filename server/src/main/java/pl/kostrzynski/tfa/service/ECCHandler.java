package pl.kostrzynski.tfa.service;

import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.entity.Payload;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

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

    public boolean isValidSignature(byte[] signature, SmartphoneDetails smartphoneDetails,
                                    SecondAuth secondAuth, Payload payload) {

        // TODO fix this not sure if the publicKey must be created here
        PublicKey publicKey = getPublicKeyFromBytes(secondAuth.getPublicKeyBytes());
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA512withECDSA");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(getMessage(smartphoneDetails, payload).getBytes());
            return ecdsaVerify.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new SecurityException("Something went wrong please try again later");
        }
    }

    private String getMessage(SmartphoneDetails smartphoneDetails, Payload payload) {
        return payload.getValue() + smartphoneDetails.getSmartphoneDetails();
    }

    public boolean isValidPublicKey(byte[] publicKey) {
        getPublicKeyFromBytes(publicKey);
        return true;
    }

}
