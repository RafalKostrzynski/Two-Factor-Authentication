package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.entity.Payload;

import java.util.Optional;

public interface PayloadRepository extends JpaRepository<Payload, Long> {
    Optional<Payload>findByUser_Username(String username);
}
