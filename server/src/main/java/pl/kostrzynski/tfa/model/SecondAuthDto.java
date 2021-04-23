package pl.kostrzynski.tfa.model;

import lombok.Data;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;

import javax.validation.constraints.NotNull;

@Data
public class SecondAuthDto {
    @NotNull(message = "Please provide public key")
    private SecondAuth secondAuth;
    @NotNull(message = "Please provide smartphone details")
    private SmartphoneDetails smartphoneDetails;
}
