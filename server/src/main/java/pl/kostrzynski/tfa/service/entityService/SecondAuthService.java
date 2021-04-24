package pl.kostrzynski.tfa.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.SecondAuthDto;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.repository.SecondAuthRepository;
import pl.kostrzynski.tfa.service.ECCHandler;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class SecondAuthService {

    private final SecondAuthRepository secondAuthRepository;
    private final SmartphoneDetailsService smartphoneDetailsService;
    private final UserService userService;
    private final ECCHandler eccHandler;
    private final SecondAuthTokenService secondAuthTokenService;

    @Autowired
    public SecondAuthService(SecondAuthRepository secondAuthRepository, SmartphoneDetailsService smartphoneDetailsService,
                             UserService userService, ECCHandler eccHandler, SecondAuthTokenService secondAuthTokenService) {
        this.secondAuthRepository = secondAuthRepository;
        this.smartphoneDetailsService = smartphoneDetailsService;
        this.userService = userService;
        this.eccHandler = eccHandler;
        this.secondAuthTokenService = secondAuthTokenService;
    }

    public SecondAuth getSecondAuthByUser(User user) {
        return secondAuthRepository.findSecondAuthByUser(user).orElseThrow(() ->
                new NoSuchElementException("No SecondAuth for user " + user.getUsername() + " found"));
    }

    public SecondAuth getSecondAuthByUsername(String username) {
        return secondAuthRepository.findSecondAuthByUser_Username(username).orElseThrow(() ->
                new NoSuchElementException("No SecondAuth for user " + username + " found"));
    }

    public void addSecondAuth(String token, SecondAuthDto secondAuthDto) {
        SecondAuth secondAuth = secondAuthDto.getSecondAuth();
        if (eccHandler.isValidPublicKey(secondAuth.getPublicKeyBytes())) {
            User user = userService.verifyToken(token, "add-public");
            secondAuth.setUser(user);
            SecondAuth dbSecondAuth = secondAuthRepository.save(secondAuth);
            smartphoneDetailsService.addSmartphoneDetails(secondAuthDto.getSmartphoneDetails(), dbSecondAuth);
        }
    }

    public SecondAuth changeKeyStatus(User user, boolean changeKey) {
        SecondAuth secondAuth = getSecondAuthByUser(user);
        secondAuth.setChangeKey(changeKey);
        return secondAuthRepository.save(secondAuth);
    }

    public void updateSecondAuth(String token, SecondAuthDto secondAuthDto) {
        SecondAuth secondAuth = secondAuthDto.getSecondAuth();
        SecondAuth databaseSecondAuth = secondAuthTokenService.getSecondAuthByToken(token);
        SecondAuth changedSecondAUth = changeSecondAuth(databaseSecondAuth, secondAuth);
        smartphoneDetailsService.updateSmartphoneDetails(secondAuthDto.getSmartphoneDetails(), changedSecondAUth);
    }

    public String generatePayload() {
        String chars = "abcdefghijklmnopqrstuvwxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789!@%$%&^?|~#+=";

        final int PW_LENGTH = 20;
        Random rnd = new SecureRandom();
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < PW_LENGTH; i++)
            pass.append(chars.charAt(rnd.nextInt(chars.length())));
        return pass.toString();
    }

    private SecondAuth changeSecondAuth(SecondAuth oldSecondAuth, SecondAuth newSecondAuth) {
        if (oldSecondAuth.isChangeKey()) {
            oldSecondAuth.setPublicKeyBytes(newSecondAuth.getPublicKeyBytes());
            oldSecondAuth.setChangeKey(false);
            return secondAuthRepository.save(oldSecondAuth);
        } else throw new ApiMethodException("Can't change key", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
