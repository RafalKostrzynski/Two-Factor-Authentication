package pl.kostrzynski.tfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.SecondAuth;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.service.SecondAuthService;
import pl.kostrzynski.tfa.service.UserService;
import pl.kostrzynski.tfa.service.VerificationTokenService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "tfa/service/rest/v1/second-auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecondFactorApi {

    private final SecondAuthService secondAuthService;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public SecondFactorApi(SecondAuthService secondAuthService, UserService userService,
                           VerificationTokenService verificationTokenService) {
        this.secondAuthService = secondAuthService;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<HttpStatus> addPublicKey(@PathVariable String token, @RequestBody @Valid SecondAuth secondAuth) {
        secondAuthService.addSecondAuth(token, secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PutMapping("/{token}")
    public ResponseEntity<HttpStatus> updatePublicKey(@PathVariable String token, @RequestBody @Valid SecondAuth secondAuth) {
        // TODO validate if user is logged in

        User user = verificationTokenService.findUserByVerificationToken(token);
        secondAuthService.updateSecondAuth(user, secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/new-key-gen")
    public ResponseEntity<String> createTokenForNewKey(@RequestBody @Valid User user,
                                                       HttpServletRequest httpServletRequest) {
        // TODO validate if user is logged in

        User databaseUser = userService.getUserByUsername(user.getUsername());
        secondAuthService.changeKeyStatus(databaseUser, true);
        return new ResponseEntity<>(
                verificationTokenService.createUUIDLink(user, "check-key-gen",
                        httpServletRequest),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("/check-key-gen/{token}")
    public ResponseEntity<HttpStatus> verifyKeyGenerationRequest(@PathVariable String token) {
        // TODO validate if user is logged in
        User user = verificationTokenService.findUserByVerificationToken(token);
        SecondAuth secondAuth = secondAuthService.getSecondAuthByUser(user);
        if (secondAuth.isChangeKey()) return new ResponseEntity<>(HttpStatus.ACCEPTED);

        throw new ApiMethodException("Generation declined", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
