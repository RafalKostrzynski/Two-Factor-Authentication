package pl.kostrzynski.tfa.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.jwt.JwtTokenService;
import pl.kostrzynski.tfa.model.QrCodeDetail;
import pl.kostrzynski.tfa.model.SecondAuthDto;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.model.enums.AuthenticationState;
import pl.kostrzynski.tfa.model.enums.QrPurpose;
import pl.kostrzynski.tfa.service.entityService.SecondAuthService;
import pl.kostrzynski.tfa.service.entityService.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;

/**
 * This is the API for operations requested by authenticated users
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
@RestController
@RequestMapping(value = "tfa/service/rest/v1/for-user", produces = MediaType.APPLICATION_JSON_VALUE)
public class AfterAuthApi {

    private final SecondAuthService secondAuthService;
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public AfterAuthApi(SecondAuthService secondAuthService, JwtTokenService jwtTokenService, UserService userService) {
        this.secondAuthService = secondAuthService;
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @PutMapping("user")
    public ResponseEntity<User> updateUser(Principal principal, @RequestBody @Valid User user) {
        return new ResponseEntity<>(userService.updateUser(principal.getName(), user), HttpStatus.ACCEPTED);
    }

    // update key after security checks
    @PutMapping("pub-key/update")
    public ResponseEntity<HttpStatus> updatePublicKey(Principal principal,
                                                      @RequestBody @Valid SecondAuthDto secondAuthDto) {
        SecondAuth dbSecondAuth = secondAuthService.getSecondAuthByUsername(principal.getName());
        secondAuthService.updateSecondAuth(dbSecondAuth, secondAuthDto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    // request from authenticated user to change key
    @GetMapping("pub-key/new-key-gen")
    public ResponseEntity<QrCodeDetail> createTokenForNewKey(Principal principal) {
        String jwtTokenMobile = jwtTokenService.createToken((Authentication) principal, AuthenticationState.MOBILE_AUTHENTICATED);
        return new ResponseEntity<>(new QrCodeDetail(QrPurpose.CHANGE_KEY, "NONE",
                jwtTokenMobile, LocalDateTime.now().plusSeconds(60).toString()),
                HttpStatus.ACCEPTED);
    }

    @GetMapping("pub-key/update/request")
    public ResponseEntity<HttpStatus> verifyRequest() {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
