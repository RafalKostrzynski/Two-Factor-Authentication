package pl.kostrzynski.tfa.service.entityService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.entity.Payload;
import pl.kostrzynski.tfa.repository.PayloadRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PayloadService {

    private final PayloadRepository payloadRepository;
    private final UserService userService;

    public PayloadService(PayloadRepository payloadRepository, UserService userService) {
        this.payloadRepository = payloadRepository;
        this.userService = userService;
    }

    public Payload getPayloadByUsername(String username) {
        return payloadRepository.findByUser_Username(username)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find payload"));
    }

    // find user then save new payload
    @Async
    public void setPayload(String payloadValue, String username, boolean active, LocalDateTime expirationTime) {
        payloadRepository.findByUser_Username(username).map(e -> updatePayload(e, payloadValue, active, expirationTime))
                .orElseGet(() -> savePayload(new Payload(payloadValue, active, expirationTime, userService.getUserByUsername(username))));
    }

    @Async
    public void changeActiveState(Payload payload, boolean active) {
        payload.setActive(active);
        savePayload(payload);
    }

    @Async
    public void payloadWasUsed(Payload payload) {
        payload.setExpirationTime(LocalDateTime.now());
        changeActiveState(payload, false);
    }

    public String generatePayload() {
        String chars = "abcdefghijklmnopqrstuvwxyz"
                + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789!@%$%&^?|~#+=";

        final int PW_LENGTH = 20;
        Random random = new SecureRandom();
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < PW_LENGTH; i++)
            pass.append(chars.charAt(random.nextInt(chars.length())));
        return pass.toString();
    }

    private boolean updatePayload(Payload databasePayload, String newPayloadValue, boolean active, LocalDateTime expirationTime) {
        databasePayload.setValue(newPayloadValue);
        databasePayload.setActive(active);
        databasePayload.setExpirationTime(expirationTime);
        return savePayload(databasePayload);
    }

    private boolean savePayload(Payload payload) {
        payloadRepository.save(payload);
        return true;
    }
}
