package pl.kostrzynski.tfa.service.entityService;

import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SecondAuthToken;
import pl.kostrzynski.tfa.repository.SecondAuthTokenRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class SecondAuthTokenService {

    private final SecondAuthTokenRepository secondAuthTokenRepository;

    public SecondAuthTokenService(SecondAuthTokenRepository secondAuthTokenRepository) {
        this.secondAuthTokenRepository = secondAuthTokenRepository;
    }

    public String createUUIDLink(SecondAuth secondAuth, String purpose, HttpServletRequest httpServletRequest) {
        String token = UUID.randomUUID().toString();
        SecondAuthToken secondAuthToken = new SecondAuthToken(token, secondAuth);
        secondAuthTokenRepository.save(secondAuthToken);
        return "https://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getContextPath()
                + "/tfa/service/rest/v1/for-user/" + purpose + "/" + token;
    }

    public SecondAuth getSecondAuthByToken(String token) {
        SecondAuthToken secondAuthToken = secondAuthTokenRepository.findByValue(token)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find provided token"));
        if (secondAuthToken.isNotExpired()) return secondAuthToken.getSecondAuth();
        throw new ApiMethodException("Token expired", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
