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

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "tfa/service/rest/v1",produces = MediaType.APPLICATION_JSON_VALUE)
public class Api {

    private final SecondAuthService secondAuthService;
    private final UserService userService;

    @Autowired
    public Api(SecondAuthService secondAuthService, UserService userService) {
        this.secondAuthService = secondAuthService;
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<HttpStatus> addUser(@RequestBody User user, HttpServletRequest httpServletRequest) throws MessagingException {
        userService.addNewUser(user,httpServletRequest);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/second-auth")
    public ResponseEntity<HttpStatus> addSecondAuth(@RequestBody SecondAuth secondAuth){
        secondAuthService.addSecondAuth(secondAuth);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
