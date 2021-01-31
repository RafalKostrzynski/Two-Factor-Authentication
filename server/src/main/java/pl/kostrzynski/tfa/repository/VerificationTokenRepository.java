package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByValue(String value);
}
