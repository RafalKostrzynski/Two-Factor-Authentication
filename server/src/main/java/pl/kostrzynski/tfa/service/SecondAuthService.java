package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.SecondAuth;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.repository.SecondAuthRepository;

import java.util.NoSuchElementException;

@Service
public class SecondAuthService {

    private final SecondAuthRepository secondAuthRepository;
    private final UserService userService;

    @Autowired
    public SecondAuthService(SecondAuthRepository secondAuthRepository, UserService userService) {
        this.secondAuthRepository = secondAuthRepository;
        this.userService = userService;
    }

    public SecondAuth getSecondAuthByUser(User user) {
        return secondAuthRepository.findSecondAuthByUser(user).orElseThrow(() ->
                new NoSuchElementException("No SecondAuth for user " + user.getUsername() + " found"));
    }

    public void addSecondAuth(String token, SecondAuth secondAuth) {
        //TODO verify if public Key is a real key
        User user = userService.verifyToken(token, "add-public");
        secondAuth.setUser(user);
        secondAuthRepository.save(secondAuth);
    }
}
