package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.repository.SecondAuthRepository;

import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class SecondAuthService {

    private final SecondAuthRepository secondAuthRepository;
    private final UserService userService;
    private final ECCHandler eccHandler;
    private final SecondAuthTokenService secondAuthTokenService;

    @Autowired
    public SecondAuthService(SecondAuthRepository secondAuthRepository, UserService userService, ECCHandler eccHandler, SecondAuthTokenService secondAuthTokenService) {
        this.secondAuthRepository = secondAuthRepository;
        this.userService = userService;
        this.eccHandler = eccHandler;
        this.secondAuthTokenService = secondAuthTokenService;
    }

    public SecondAuth getSecondAuthByUser(User user) {
        return secondAuthRepository.findSecondAuthByUser(user).orElseThrow(() ->
                new NoSuchElementException("No SecondAuth for user " + user.getUsername() + " found"));
    }

    public void addSecondAuth(String token, SecondAuth secondAuth) {
        if (eccHandler.isValidPublicKey(secondAuth.getPublicKeyBytes())) {
            User user = userService.verifyToken(token, "add-public");
            secondAuth.setUser(user);
            secondAuthRepository.save(secondAuth);
        }
    }

    public SecondAuth changeKeyStatus(User user, boolean changeKey) {
        SecondAuth secondAuth = getSecondAuthByUser(user);
        secondAuth.setChangeKey(changeKey);
        return secondAuthRepository.save(secondAuth);
    }

    public void updateSecondAuth(String token, SecondAuth secondAuth) {
        SecondAuth databaseSecondAuth = secondAuthTokenService.getSecondAuthByToken(token);
        changeSecondAuth(databaseSecondAuth, secondAuth);
    }

    public String generatePayload() {
        String chars = "abcdefghijklmnopqrstuvwxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789!@%$%&^?|~'#+=";

        final int PW_LENGTH = 30;
        Random rnd = new Random();
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < PW_LENGTH; i++)
            pass.append(chars.charAt(rnd.nextInt(chars.length())));
        return pass.toString();
    }

    private void changeSecondAuth(SecondAuth oldSecondAuth, SecondAuth newSecondAuth) {
        if (oldSecondAuth.isChangeKey()) {
            oldSecondAuth.setPublicKeyBytes(newSecondAuth.getPublicKeyBytes());
            oldSecondAuth.setChangeKey(false);
            secondAuthRepository.save(oldSecondAuth);
        } else throw new ApiMethodException("Can't change key", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
