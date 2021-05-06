package pl.kostrzynski.twofactorauthentication.model;

import lombok.Data;

@Data
public class SecondAuthDto {
    private SecondAuth secondAuth;
    private SmartphoneDetails smartphoneDetails;

    public SecondAuthDto(SecondAuth secondAuth, SmartphoneDetails smartphoneDetails) {
        this.secondAuth = secondAuth;
        this.smartphoneDetails = smartphoneDetails;
    }

}
