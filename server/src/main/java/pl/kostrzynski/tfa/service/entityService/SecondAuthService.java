package pl.kostrzynski.tfa.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.to.SecondAuthDto;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.repository.SecondAuthRepository;
import pl.kostrzynski.tfa.service.ECCHandler;

import java.util.NoSuchElementException;

@Service
public class SecondAuthService {

    private final SecondAuthRepository secondAuthRepository;
    private final SmartphoneDetailsService smartphoneDetailsService;
    private final UserService userService;
    private final ECCHandler eccHandler;

    @Autowired
    public SecondAuthService(SecondAuthRepository secondAuthRepository, SmartphoneDetailsService smartphoneDetailsService,
                             UserService userService, ECCHandler eccHandler) {
        this.secondAuthRepository = secondAuthRepository;
        this.smartphoneDetailsService = smartphoneDetailsService;
        this.userService = userService;
        this.eccHandler = eccHandler;
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

    public void updateSecondAuth(SecondAuth databaseSecondAuth, SecondAuthDto secondAuthDto) {
        SecondAuth secondAuth = secondAuthDto.getSecondAuth();
        SecondAuth changedSecondAuth = changeSecondAuth(databaseSecondAuth, secondAuth);
        smartphoneDetailsService.updateSmartphoneDetails(secondAuthDto.getSmartphoneDetails(), changedSecondAuth);
    }

    private SecondAuth changeSecondAuth(SecondAuth oldSecondAuth, SecondAuth newSecondAuth) {
        oldSecondAuth.setPublicKeyBytes(newSecondAuth.getPublicKeyBytes());
        return secondAuthRepository.save(oldSecondAuth);
    }
}
