package pl.kostrzynski.tfa.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.service.SecondAuthService;

import javax.validation.Valid;

/**
 * This is the API for the second authentication step
 * <p>
 * See the swagger documentation on the API methods under https://localhost:8443/swagger-ui/index.html
 */
@RestController
@RequestMapping(value = "tfa/service/rest/v1/second-auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecondFactorApi {

    private final SecondAuthService secondAuthService;

    @Autowired
    public SecondFactorApi(SecondAuthService secondAuthService) {
        this.secondAuthService = secondAuthService;
    }

    @PostMapping("{token}")
    public ResponseEntity<HttpStatus> addPublicKey(@PathVariable String token, @RequestBody @Valid SecondAuth secondAuth) {
        secondAuthService.addSecondAuth(token, secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
