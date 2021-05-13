package pl.kostrzynski.tfa.model.to;

import lombok.Data;
import pl.kostrzynski.tfa.model.entity.SecondAuth;
import pl.kostrzynski.tfa.model.entity.SmartphoneDetails;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class SecondAuthDto {
    @NotNull(message = "Please provide public key")
    @Valid
    private SecondAuth secondAuth;
    @NotNull(message = "Please provide smartphone details")
    @Valid
    private SmartphoneDetails smartphoneDetails;
}
