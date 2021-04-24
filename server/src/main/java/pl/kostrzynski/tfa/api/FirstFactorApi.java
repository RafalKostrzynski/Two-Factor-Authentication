package pl.kostrzynski.tfa.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import pl.kostrzynski.tfa.service.SecondAuthService;
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
// TODO change endpoint to ...unauthorized?
@RestController
@RequestMapping(value = "tfa/service/rest/v1/first-auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class FirstFactorApi {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final SecondAuthService secondAuthService;

    @Autowired
    public FirstFactorApi(UserService userService,
                          VerificationTokenService verificationTokenService, AuthenticationManager authenticationManager,
                          JwtTokenService jwtTokenService, SecondAuthService secondAuthService) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.secondAuthService = secondAuthService;
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
        if (databaseUser.isEmailVerified()) throw new ApiMethodException("This users email address already is verified",
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
    public ResponseEntity<AuthenticationResponse> initializeLogin(@Valid @RequestBody User user) throws JsonProcessingException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        String payload = secondAuthService.generatePayload();
        String jwtTokenMobile = jwtTokenService.createToken(authentication, AuthenticationState.MOBILE);
        ObjectMapper objectMapper = new ObjectMapper();
        String qrCode = objectMapper.writeValueAsString(new QrCodeDetail(payload, jwtTokenMobile));
        String jwtTokenWeb = jwtTokenService.createToken(authentication, AuthenticationState.PRE_AUTHENTICATED);

        return new ResponseEntity<>(new AuthenticationResponse(jwtTokenWeb, qrCode,
                30),
                HttpStatus.ACCEPTED);
    }
}
