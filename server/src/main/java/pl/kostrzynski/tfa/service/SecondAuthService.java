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

    @Autowired
    public SecondAuthService(SecondAuthRepository secondAuthRepository) {
        this.secondAuthRepository = secondAuthRepository;
    }

    public SecondAuth getSecondAuthByUser(User user){
        return secondAuthRepository.findSecondAuthByUser(user).orElseThrow(()->
                new NoSuchElementException("No SecondAuth for user "+user.getUsername()+" found"));
    }

    public void addSecondAuth(SecondAuth secondAuth){
        secondAuthRepository.save(secondAuth);
    }
}