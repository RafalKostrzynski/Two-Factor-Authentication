package pl.kostrzynski.tfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.service.UserService;
import pl.kostrzynski.tfa.service.VerificationTokenService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * This is the API for the first authentication step
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
@RestController
@RequestMapping(value = "tfa/service/rest/v1/first-auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class FirstFactorApi {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;


    @Autowired
    public FirstFactorApi(UserService userService,
                          VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("user")
    public ResponseEntity<HttpStatus> addUser(@Valid @RequestBody User user, HttpServletRequest httpServletRequest)
            throws MessagingException {
        userService.addNewUser(user, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // it is used when user didn't received the verification email
    @PatchMapping("verification-mail")
    public ResponseEntity<HttpStatus> sendVerificationMail(@Valid @RequestBody User user, HttpServletRequest httpServletRequest)
            throws MessagingException {
        User databaseUser = userService.getUserByUsername(user.getUsername());
        if (user.isEmailVerified()) throw new ApiMethodException("This users email address already is verified",
                ApiErrorCodeEnum.FORBIDDEN);

        userService.sendVerificationEmail(databaseUser, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("verify-email/{token}")
    public ResponseEntity<String> verifyEmailAndGenerateTokenForNewPublicKey(@PathVariable String token,
                                                                             HttpServletRequest httpServletRequest) {
        User user = userService.verifyToken(token, "verify-email");
        if (user.isEmailVerified()) return new ResponseEntity<>(
                verificationTokenService.createUUIDLink(user, "add-public", httpServletRequest),
                HttpStatus.ACCEPTED);

        throw new ApiMethodException("Email could not be verified, try again", ApiErrorCodeEnum.FORBIDDEN);
    }

//    @GetMapping("/login")
//    public ResponseEntity<SessionInitializerObject> initializeLoginSession(@Valid @RequestBody User user,
//                                                                           HttpServletRequest httpServletRequest){
//    }
}
