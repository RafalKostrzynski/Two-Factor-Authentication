package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.entity.SecondAuthToken;

import java.util.Optional;

public interface SecondAuthTokenRepository extends JpaRepository<SecondAuthToken, Long> {
    Optional<SecondAuthToken> findByValue(String token);
}
