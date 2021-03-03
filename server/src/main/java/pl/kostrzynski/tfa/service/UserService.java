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
    private final VerificationTokenService tokenRepositoryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService mailSenderService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       VerificationTokenService tokenRepositoryService, MailSenderService mailSenderService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepositoryService = tokenRepositoryService;
        this.mailSenderService = mailSenderService;
    }

    public void addNewUser(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        String url = tokenRepositoryService.createUUIDLink(user, httpServletRequest);
        mailSenderService.sendMail(user.getUsername(), "Verification Token", url, false);
    }

    public void verifyToken(String token) {
        User user = tokenRepositoryService.findUserByVerificationToken(token);
        if(user.isEmailVerified()) user.setEnabled(true);
        else user.setEmailVerified(true);
        userRepository.save(user);
    }
}
