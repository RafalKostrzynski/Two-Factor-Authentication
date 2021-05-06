package pl.kostrzynski.tfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.jwt.JwtTokenService;
import pl.kostrzynski.tfa.model.AuthenticationResponse;
import pl.kostrzynski.tfa.model.QrCodeDetail;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.model.enums.AuthenticationState;
import pl.kostrzynski.tfa.model.enums.QrPurpose;
import pl.kostrzynski.tfa.service.entityService.PayloadService;
import pl.kostrzynski.tfa.service.entityService.UserService;
import pl.kostrzynski.tfa.service.entityService.VerificationTokenService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * This is the API for the first authentication step
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
// TODO change endpoint to ...unauthorized?
@RestController
@RequestMapping(value = "tfa/service/rest/v1/first-auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class FirstFactorApi {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final PayloadService payloadService;

    @Autowired
    public FirstFactorApi(UserService userService,
                          VerificationTokenService verificationTokenService, AuthenticationManager authenticationManager,
                          JwtTokenService jwtTokenService, PayloadService payloadService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.payloadService = payloadService;
    }

    @PostMapping("user")
    public ResponseEntity<HttpStatus> addUser(@Valid @RequestBody User user, HttpServletRequest httpServletRequest)
            throws MessagingException {
        userService.addNewUser(user, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // it is used when user didn't received the verification email
    @PatchMapping("verification-mail")
    public ResponseEntity<HttpStatus> sendVerificationMail(@RequestParam String email, HttpServletRequest httpServletRequest)
            throws MessagingException {
        User databaseUser = userService.getUserByEmail(email);
        if (databaseUser.isEmailVerified()) throw new ApiMethodException("Can't verify this user. " +
                "User doesn't exist or is already verified",
                ApiErrorCodeEnum.FORBIDDEN);

        userService.sendVerificationEmail(databaseUser, httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("verify-email/{token}")
    public ResponseEntity<String> verifyEmailAndGenerateTokenForNewPublicKey(@PathVariable String token,
                                                                             HttpServletRequest httpServletRequest) {
        User user = userService.verifyToken(token, "verify-email");
        return new ResponseEntity<>(
                verificationTokenService.createUUIDLink(user, "add-public", httpServletRequest),
                HttpStatus.ACCEPTED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthenticationResponse> initializeLogin(@Valid @RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        String payload = payloadService.generatePayload();
        String jwtTokenMobile = jwtTokenService.createToken(authentication, AuthenticationState.MOBILE_PRE_AUTHENTICATED);
        QrCodeDetail qrCode = new QrCodeDetail(
                QrPurpose.AUTHENTICATE, payload, jwtTokenMobile, LocalDateTime.now().plusSeconds(60).toString());
        String jwtTokenWeb = jwtTokenService.createToken(authentication, AuthenticationState.PRE_AUTHENTICATED);
        payloadService.setPayload(payload, user.getUsername(), false, LocalDateTime.now().plusSeconds(65));

        return new ResponseEntity<>(new AuthenticationResponse(jwtTokenWeb, qrCode,
                60),
                HttpStatus.ACCEPTED);
    }
}
