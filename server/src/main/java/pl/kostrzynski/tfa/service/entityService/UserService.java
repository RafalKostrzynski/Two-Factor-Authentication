package pl.kostrzynski.tfa.service.entityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.exception.ApiErrorCodeEnum;
import pl.kostrzynski.tfa.exception.ApiMethodException;
import pl.kostrzynski.tfa.model.entity.User;
import pl.kostrzynski.tfa.repository.UserRepository;
import pl.kostrzynski.tfa.service.MailSenderService;

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
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ApiMethodException(String.format("Username %s not found", username), ApiErrorCodeEnum.NOT_FOUND));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ApiMethodException(String.format("Email %s not found", email), ApiErrorCodeEnum.NOT_FOUND));
    }

    public void addNewUser(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        sendVerificationEmail(user, httpServletRequest);
    }

    @Async
    public void storeToken(String jwtToken, String username) {
        User user = getUserByUsername(username);
        user.setJwt(passwordEncoder.encode(jwtToken));
        userRepository.save(user);
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

    public void sendPasswordResetMail(User user, HttpServletRequest httpServletRequest) throws MessagingException {
        String url = verificationTokenService.createUUIDLink(user, "reset-password", httpServletRequest);
        String textMessage = "Hello " + user.getUsername() + "!\n\n" +
                "Someone with requested a password reset with this email address.\n" +
                "Click on this link address to reset your current password.\n\n" + url
                + "\n\nIf this request was not provided by you, ignore this message" +
                "This token is available for 24 hours, after this it will expire\n\n" +
                "After entering the link you will have 15 minutes to create a new password";
        mailSenderService.sendMail(user.getEmail(), "Reset password",
                textMessage, false);
    }

    public User updateUser(String username, User updatedUser) {
        return userRepository.findByUsername(username).map(e -> changeUser(e, updatedUser))
                .orElseThrow(
                        () -> new ApiMethodException(String.format("Username %s not found", username), ApiErrorCodeEnum.NOT_FOUND));
    }

    private User changeUser(User dbUser, User updatedUser) {
        dbUser.setUsername(updatedUser.getUsername());
        dbUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        dbUser.setEmail(updatedUser.getEmail());
        return userRepository.save(dbUser);
    }

    public User verifyToken(String token, String purpose) {
        User user = verificationTokenService.getUserByVerificationToken(token);
        if (purpose.equals("verify-email") && !user.isEmailVerified()) user.setEmailVerified(true);
        else if (user.isEmailVerified() && !user.isEnabled() && purpose.equals("add-public")) {
            verificationTokenService.deleteToken(token);
            user.setEnabled(true);
        } else throw new IllegalArgumentException("Couldn't verify token");
        return userRepository.save(user);
    }

    public void changePassword(String username, String password) {
        userRepository.findByUsername(username).map(e -> {
            e.setPassword(passwordEncoder.encode(password));
            return userRepository.save(e);
        }).orElseThrow(
                () -> new ApiMethodException(String.format("Username %s not found", username), ApiErrorCodeEnum.NOT_FOUND));
    }
}
