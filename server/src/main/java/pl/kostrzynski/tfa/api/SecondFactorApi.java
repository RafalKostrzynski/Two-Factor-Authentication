package pl.kostrzynski.tfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.jwt.JwtTokenService;
import pl.kostrzynski.tfa.model.AuthenticationResponse;
import pl.kostrzynski.tfa.model.SecondAuthDto;
import pl.kostrzynski.tfa.model.entity.Payload;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;
import pl.kostrzynski.tfa.model.enums.AuthenticationState;
import pl.kostrzynski.tfa.service.ECCHandler;
import pl.kostrzynski.tfa.service.entityService.PayloadService;
import pl.kostrzynski.tfa.service.entityService.SecondAuthService;
import pl.kostrzynski.tfa.service.entityService.SmartphoneDetailsService;
import pl.kostrzynski.tfa.service.entityService.UserService;

import javax.validation.Valid;
import java.security.Principal;

/**
 * This is the API for the second authentication step
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
@RestController
@RequestMapping(value = "tfa/service/rest/v1/second-auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecondFactorApi {

    private final SecondAuthService secondAuthService;
    private final SmartphoneDetailsService smartphoneDetailsService;
    private final PayloadService payloadService;
    private final JwtTokenService jwtTokenService;
    private final ECCHandler eccHandler;
    private final UserService userService;

    @Autowired
    public SecondFactorApi(SecondAuthService secondAuthService,
                           SmartphoneDetailsService smartphoneDetailsService,
                           PayloadService payloadService, JwtTokenService jwtTokenService,
                           ECCHandler eccHandler, UserService userService) {
        this.secondAuthService = secondAuthService;
        this.smartphoneDetailsService = smartphoneDetailsService;
        this.payloadService = payloadService;
        this.jwtTokenService = jwtTokenService;
        this.eccHandler = eccHandler;
        this.userService = userService;
    }

    @PostMapping("{token}")
    public ResponseEntity<HttpStatus> addPublicKey(@PathVariable String token, @RequestBody @Valid SecondAuthDto secondAuthDto) {
        secondAuthService.addSecondAuth(token, secondAuthDto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // Method for mobile authentication
    @PostMapping("verify")
    public ResponseEntity<HttpStatus> verifyUser(Principal principal, @RequestBody String signature) {
        // verify signature
        SecondAuth secondAuth = secondAuthService.getSecondAuthByUsername(principal.getName());
        SmartphoneDetails smartphoneDetails = smartphoneDetailsService.getSmartphoneDetailsBySecondAuthId(secondAuth.getId());
        Payload payload = payloadService.getPayloadByUsername(principal.getName());
        if (eccHandler.isValidSignature(signature, smartphoneDetails, secondAuth, payload)) {
            payloadService.changeActiveState(payload, true);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        throw new ApiMethodException("Signature not valid", ApiErrorCodeEnum.FORBIDDEN);
    }

    // Method for mobile password change
    @PostMapping("change-password")
    public ResponseEntity<HttpStatus> changePassword(Principal principal,
                                                     @RequestBody String signature, @RequestParam String password) {
        if (password.length() < 9 || password.length() > 60)
            throw new ApiMethodException("Password not valid", ApiErrorCodeEnum.NOT_ACCEPTABLE);
        SecondAuth secondAuth = secondAuthService.getSecondAuthByUsername(principal.getName());
        SmartphoneDetails smartphoneDetails = smartphoneDetailsService.getSmartphoneDetailsBySecondAuthId(secondAuth.getId());
        Payload payload = payloadService.getPayloadByUsername(principal.getName());
        if (eccHandler.isValidSignature(signature, smartphoneDetails, secondAuth, payload) && payload.isNotExpired()) {
            userService.changePassword(principal.getName(), password);
            payloadService.payloadWasUsed(payload);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        throw new ApiMethodException("Signature not valid", ApiErrorCodeEnum.FORBIDDEN);
    }

    // Method for web
    @PostMapping("authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(Principal principal) {
        Payload payload = payloadService.getPayloadByUsername(principal.getName());
        if (payload.isActive() && payload.isNotExpired()) {
            payloadService.payloadWasUsed(payload);
            String jwtToken = jwtTokenService.createToken((Authentication) principal, AuthenticationState.AUTHENTICATED);
            return new ResponseEntity<>(new AuthenticationResponse(jwtToken, 14), HttpStatus.ACCEPTED);
        }
        throw new ApiMethodException("Token expired", ApiErrorCodeEnum.FORBIDDEN);
    }
}
