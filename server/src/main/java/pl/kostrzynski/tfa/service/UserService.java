package pl.kostrzynski.tfa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.model.VerificationToken;
import pl.kostrzynski.tfa.repository.UserRepository;
import pl.kostrzynski.tfa.repository.VerificationTokenRepository;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class UserService {
    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       VerificationTokenRepository tokenRepository, MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.mailSenderService = mailSenderService;
    }

    public void addNewUser(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        tokenRepository.save(verificationToken);
        String url = "http://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort() + httpServletRequest.getContextPath()
                + "/verify-token?token=" + token;
        mailSenderService.sendMail(user.getUsername(), "Verification Token", url, false);
    }

    public void verifyToken(String token) {
        User user = tokenRepository.findByValue(token).getUser();
        user.setEnabled(true);
        userRepository.save(user);
    }
}
