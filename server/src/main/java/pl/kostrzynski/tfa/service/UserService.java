package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.repository.UserRepository;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserService {
    private final VerificationTokenService verificationTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       VerificationTokenService verificationTokenService, MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.mailSenderService = mailSenderService;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void addNewUser(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        sendVerificationEmail(user, httpServletRequest);
    }

    public void sendVerificationEmail(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        String url = verificationTokenService.createUUIDLink(user, "verify-email", httpServletRequest);
        String textMessage = "Nice to meet you " + user.getUsername() + "!\n\n" +
                "This is your Verification Token.\n" +
                "Please enter it to verify your email address.\n\n" + url
                + "\n\nThis token is available for 24 hours, after this it will expire";
        mailSenderService.sendMail(user.getEmail(), "Verification Token",
                textMessage, false);
    }

    // Took out because it might be useful in the future
//    public boolean userExistsForLaterVerificationMail(User user) {
//        ExampleMatcher modelMatcher = ExampleMatcher.matching()
//                .withIgnorePaths("id","password");
//        user.setEmailVerified(false);
//        return userExists(user, modelMatcher);
//    }
//
//    public boolean userExists(User user, ExampleMatcher modelMatcher) {
//        Example<User> userExample = Example.of(user, modelMatcher);
//        return userRepository.exists(userExample);
//    }

    public User verifyToken(String token, String purpose) {
        User user = verificationTokenService.getUserByVerificationToken(token);
        if (purpose.equals("verify-email") && !user.isEmailVerified()) user.setEmailVerified(true);
        else if (user.isEmailVerified() && !user.isEnabled() && purpose.equals("add-public")) user.setEnabled(true);
        else throw new IllegalArgumentException("Couldn't verify token");
        return userRepository.save(user);
    }
}
