package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.SecondAuth;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.repository.SecondAuthRepository;

import java.util.NoSuchElementException;

@Service
public class SecondAuthService {

    private final SecondAuthRepository secondAuthRepository;
    private final UserService userService;
    private final ECCHandler eccHandler;

    @Autowired
    public SecondAuthService(SecondAuthRepository secondAuthRepository, UserService userService, ECCHandler eccHandler) {
        this.secondAuthRepository = secondAuthRepository;
        this.userService = userService;
        this.eccHandler = eccHandler;
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

    public void changeKeyStatus(User user, boolean changeKey) {
        SecondAuth secondAuth = getSecondAuthByUser(user);
        secondAuth.setChangeKey(changeKey);
        secondAuthRepository.save(secondAuth);
    }

    public void updateSecondAuth(User user, SecondAuth secondAuth){
        secondAuthRepository.findSecondAuthByUser(user)
                .map(e->changeSecondAuth(e,secondAuth)).orElseThrow(() ->
                new NoSuchElementException("No SecondAuth for user " + user.getUsername() + " found"));
    }

    private boolean changeSecondAuth(SecondAuth oldSecondAuth, SecondAuth newSecondAuth){
        if(oldSecondAuth.isChangeKey()){
            oldSecondAuth.setPublicKeyBytes(newSecondAuth.getPublicKeyBytes());
            oldSecondAuth.setChangeKey(false);
            secondAuthRepository.save(oldSecondAuth);
            return true;
        }else throw new ApiMethodException("Cant change key", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
