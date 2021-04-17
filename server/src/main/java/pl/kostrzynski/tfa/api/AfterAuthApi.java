package pl.kostrzynski.tfa.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.service.SecondAuthService;
import pl.kostrzynski.tfa.service.SecondAuthTokenService;
import pl.kostrzynski.tfa.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * This is the API for operations requested by authenticated users
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
@RestController
@RequestMapping(value = "tfa/service/rest/v1/for-user", produces = MediaType.APPLICATION_JSON_VALUE)
public class AfterAuthApi {

    private final SecondAuthService secondAuthService;
    private final UserService userService;
    private final SecondAuthTokenService secondAuthTokenService;

    public AfterAuthApi(SecondAuthService secondAuthService, UserService userService, SecondAuthTokenService secondAuthTokenService) {
        this.secondAuthService = secondAuthService;
        this.userService = userService;
        this.secondAuthTokenService = secondAuthTokenService;
    }

    // update key after security checks
    @PutMapping("pub-key/{token}")
    public ResponseEntity<HttpStatus> updatePublicKey(@PathVariable String token, @RequestBody @Valid SecondAuth secondAuth) {
        secondAuthService.updateSecondAuth(token, secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // request from authenticated user to change key
    @GetMapping("pub-key/new-key-gen")
    public ResponseEntity<String> createTokenForNewKey(@RequestBody @Valid User user,
                                                       HttpServletRequest httpServletRequest) {

        // TODO get user from session cookies not argument

        User databaseUser = userService.getUserByUsername(user.getUsername());
        SecondAuth secondAuth = secondAuthService.changeKeyStatus(databaseUser, true);
        return new ResponseEntity<>(
                secondAuthTokenService.createUUIDLink(secondAuth, "check-key-gen",
                        httpServletRequest),
                HttpStatus.ACCEPTED);
    }

    // check if the change key status has been set to true and if the token didn't expire
    @GetMapping("check-key-gen/{token}")
    public ResponseEntity<HttpStatus> verifyKeyGenerationRequest(@PathVariable String token) {
        SecondAuth secondAuth = secondAuthTokenService.getSecondAuthByToken(token);
        if (secondAuth.isChangeKey()) return new ResponseEntity<>(HttpStatus.ACCEPTED);
        throw new ApiMethodException("Generation declined", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
