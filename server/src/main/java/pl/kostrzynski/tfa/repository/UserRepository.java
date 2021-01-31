package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
