package pl.kostrzynski.tfa.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.model.entity.VerificationToken;
import pl.kostrzynski.tfa.repository.VerificationTokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user).map(e -> {
            e.setValue(token);
            e.setExpirationTime(LocalDateTime.now().plusHours(24));
            return e;
        }).orElseGet(() -> new VerificationToken(user, token));

        verificationTokenRepository.save(verificationToken);
        return "https://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getContextPath()
                + "/tfa/service/rest/v1/first-auth/" + purpose + "/" + token;
    }

    public User getUserByVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByValue(token)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided token"));
        if (verificationToken.isNotExpired()) return verificationToken.getUser();
        throw new ApiMethodException("Token expired", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }

    @Async
    public void deleteToken(String token){
        VerificationToken verificationToken = verificationTokenRepository.findByValue(token)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided token"));
        verificationTokenRepository.delete(verificationToken);
    }
}
