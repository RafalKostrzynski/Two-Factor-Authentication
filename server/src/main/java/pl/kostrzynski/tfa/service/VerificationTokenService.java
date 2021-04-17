package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
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
        VerificationToken verificationToken;

        switch (purpose) {
            case "add-public": {
                verificationToken = verificationTokenRepository.findByUser(user)
                        .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided user"));
                verificationToken.setValue(token);
                verificationToken.setExpirationTime(LocalDateTime.now().plusHours(24));
                break;
            }
            case "verify-email": {
                verificationToken = new VerificationToken(user, token);
                break;
            }
            default:
                throw new ApiMethodException("Something went wrong please try again", ApiErrorCodeEnum.NOT_ACCEPTABLE);
        }

        verificationTokenRepository.save(verificationToken);
        return "https://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getContextPath()
                + "/tfa/service/rest/v1/first-auth/" + purpose + "/" + token;
    }

    public User getUserByVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByValue(token)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided token"));
        if (verificationToken.tokenNotExpired()) return verificationToken.getUser();
        throw new ApiMethodException("Token expired", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
