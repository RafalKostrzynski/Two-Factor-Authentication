package pl.kostrzynski.tfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;

import java.util.Optional;

public interface SmartphoneDetailsRepository extends JpaRepository<SmartphoneDetails, Long> {
    Optional<SmartphoneDetails>findBySecondAuth(SecondAuth secondAuth);
    Optional<SmartphoneDetails>findBySecondAuth_Id(long secondAuthId);
}
