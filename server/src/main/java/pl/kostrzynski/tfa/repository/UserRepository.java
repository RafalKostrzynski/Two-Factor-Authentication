package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
