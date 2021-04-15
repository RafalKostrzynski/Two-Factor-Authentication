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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * This is the API for the whole authorization
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
@RestController
@RequestMapping(value = "tfa/service/rest/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class Api {

    private final SecondAuthService secondAuthService;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public Api(SecondAuthService secondAuthService, UserService userService,
               VerificationTokenService verificationTokenService) {
        this.secondAuthService = secondAuthService;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/user")
    public ResponseEntity<HttpStatus> addUser(@Valid @RequestBody User user, HttpServletRequest httpServletRequest)
            throws MessagingException {
        userService.addNewUser(user, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/second-auth/{token}")
    public ResponseEntity<HttpStatus> addPublicKey(@PathVariable String token, @RequestBody @Valid SecondAuth secondAuth) {
        secondAuthService.addSecondAuth(token, secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // is used when uses didn't received the verification email
    @PatchMapping("verification-mail")
    public ResponseEntity<HttpStatus> sendVerificationMail(@Valid @RequestBody User user, HttpServletRequest httpServletRequest)
            throws MessagingException {
        if (userService.userExistsForLaterVerificationMail(user)) {
            userService.sendVerificationEmail(user, httpServletRequest);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        throw new ApiMethodException("User does not exist please check input data",
                ApiErrorCodeEnum.NOT_FOUND);
    }

    @PutMapping("/second-auth/{token}")
    public ResponseEntity<HttpStatus> updatePublicKey(@PathVariable String token, @RequestBody @Valid SecondAuth secondAuth){
        // TODO validate if user is logged in

        User user = verificationTokenService.findUserByVerificationToken(token);
        secondAuthService.updateSecondAuth(user,secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/verify-email/{token}")
    public ResponseEntity<String> verifyEmailAndGenerateTokenForNewPublicKey(@PathVariable String token,
                                                                             HttpServletRequest httpServletRequest) {
        User user = userService.verifyToken(token, "verify-email");
        if (user.isEmailVerified()) return new ResponseEntity<>(
                verificationTokenService.createUUIDLink(user, "add-public", httpServletRequest),
                HttpStatus.ACCEPTED);

        throw new ApiMethodException("Email could not be verified, try again", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }

    @GetMapping("/second-auth/new-key-gen")
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

    @GetMapping("/second-auth/check-key-gen/{token}")
    public ResponseEntity<HttpStatus> verifyKeyGenerationRequest(@PathVariable String token) {
        // TODO validate if user is logged in
        User user = verificationTokenService.findUserByVerificationToken(token);
        SecondAuth secondAuth = secondAuthService.getSecondAuthByUser(user);
        if (secondAuth.isChangeKey()) return new ResponseEntity<>(HttpStatus.ACCEPTED);

        throw new ApiMethodException("Generation declined", ApiErrorCodeEnum.NOT_ACCEPTABLE);
    }
}
