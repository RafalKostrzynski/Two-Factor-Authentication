package pl.kostrzynski.tfa.service.entityService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;
import pl.kostrzynski.tfa.repository.SmartphoneDetailsRepository;

@Service
public class SmartphoneDetailsService {

    private final SmartphoneDetailsRepository smartphoneDetailsRepository;

    public SmartphoneDetailsService(SmartphoneDetailsRepository smartphoneDetailsRepository) {
        this.smartphoneDetailsRepository = smartphoneDetailsRepository;
    }

    @Async
    public void addSmartphoneDetails(SmartphoneDetails smartphoneDetails, SecondAuth secondAuth){
        smartphoneDetails.setSecondAuth(secondAuth);
        smartphoneDetailsRepository.save(smartphoneDetails);
    }
    @Async
    public void updateSmartphoneDetails(SmartphoneDetails smartphoneDetails, SecondAuth secondAuth) {
        smartphoneDetailsRepository.findBySecondAuth(secondAuth).map(e->changeSmartphoneDetails(e, smartphoneDetails))
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find smartphone details"));
    }

    private boolean changeSmartphoneDetails(SmartphoneDetails databaseSmartphoneDetails,
                                            SmartphoneDetails newSmartphoneDetails){
        databaseSmartphoneDetails.setAndroidID(newSmartphoneDetails.getAndroidID());
        databaseSmartphoneDetails.setBrand(newSmartphoneDetails.getBrand());
        databaseSmartphoneDetails.setManufacturer(newSmartphoneDetails.getManufacturer());
        databaseSmartphoneDetails.setType(newSmartphoneDetails.getType());
        smartphoneDetailsRepository.save(newSmartphoneDetails);
        return true;
    }
}
