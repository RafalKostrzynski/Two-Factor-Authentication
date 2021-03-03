package pl.kostrzynski.tfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.model.SecondAuth;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.service.SecondAuthService;
import pl.kostrzynski.tfa.service.UserService;
import pl.kostrzynski.tfa.service.VerificationTokenService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "tfa/service/rest/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class Api {

    private final SecondAuthService secondAuthService;
    private final UserService userService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public Api(SecondAuthService secondAuthService, UserService userService, VerificationTokenService verificationTokenService) {
        this.secondAuthService = secondAuthService;
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/user")
    public ResponseEntity<HttpStatus> addUser(@RequestBody User user, HttpServletRequest httpServletRequest) throws MessagingException {
        userService.addNewUser(user, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/second-auth/{token}")
    public ResponseEntity<HttpStatus> addPublicKey(@PathVariable String token, @RequestBody SecondAuth secondAuth) {
        // TODO Implement EMAI storing?
        secondAuthService.addSecondAuth(token, secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/verify-email/{token}")
    public ResponseEntity<String> verifyEmailAndGenerateTokenForNewPublicKey(@PathVariable String token, HttpServletRequest httpServletRequest) {
        User user = userService.verifyToken(token, "verify-email");
        //TODO test if doesn't throw errors when wrong data
        return user.isEmailVerified() ?
                new ResponseEntity<>(verificationTokenService.createUUIDLink(user, "add-public", httpServletRequest), HttpStatus.ACCEPTED) : new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
}
