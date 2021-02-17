package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.SecondAuth;
import pl.kostrzynski.tfa.model.User;

import java.util.Optional;

public interface SecondAuthRepository extends JpaRepository<SecondAuth, Long> {
    Optional<SecondAuth> findSecondAuthByUser(User user);
}
