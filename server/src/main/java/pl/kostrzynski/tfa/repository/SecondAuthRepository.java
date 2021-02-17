package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.SecondAuth;

public interface SecondAuthRepository extends JpaRepository<SecondAuth, Long> {
}
