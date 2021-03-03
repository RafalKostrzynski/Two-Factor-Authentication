package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.User;
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

    public void addNewUser(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String url = verificationTokenService.createUUIDLink(user, "verify-email", httpServletRequest);
        mailSenderService.sendMail(user.getUsername(), "Verification Token", url, false);
    }

    public User verifyToken(String token, String purpose) {
        User user = verificationTokenService.findUserByVerificationToken(token);
        if (purpose.equals("verify-email")) user.setEmailVerified(true);
        else if (user.isEmailVerified() && purpose.equals("add-public")) user.setEnabled(true);
        else throw new IllegalArgumentException("Couldn't verify token");
        return userRepository.save(user);
    }
}
