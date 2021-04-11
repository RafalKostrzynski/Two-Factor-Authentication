package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.User;
import pl.kostrzynski.tfa.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByValue(String value);
    Optional<VerificationToken> findByUser(User user);
}
