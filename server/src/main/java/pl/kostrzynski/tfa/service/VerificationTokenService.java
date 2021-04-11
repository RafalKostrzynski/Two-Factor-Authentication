package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.model.VerificationToken;
import pl.kostrzynski.tfa.repository.VerificationTokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public String createUUIDLink(User user, String purpose, HttpServletRequest httpServletRequest) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken;

        if(purpose.equals("add-public")){
            verificationToken = verificationTokenRepository.findByUser(user)
                    .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided user"));
            verificationToken.setValue(token);
        }else verificationToken = new VerificationToken(user, token);

        verificationTokenRepository.save(verificationToken);
        return "https://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getContextPath()
                + "/tfa/service/rest/v1/" + purpose + "/" + token;
    }

    public User findUserByVerificationToken(String token) {
        return verificationTokenRepository.findByValue(token)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided token"))
                .getUser();
    }
}
