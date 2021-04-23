package pl.kostrzynski.twofactorauthentication.model;

public class SecondAuthDto {
    private SecondAuth secondAuth;
    private SmartphoneDetails smartphoneDetails;

    public SecondAuthDto(SecondAuth secondAuth, SmartphoneDetails smartphoneDetails) {
        this.secondAuth = secondAuth;
        this.smartphoneDetails = smartphoneDetails;
    }

    public SecondAuth getSecondAuth() {
        return secondAuth;
    }

    public void setSecondAuth(SecondAuth secondAuth) {
        this.secondAuth = secondAuth;
    }

    public SmartphoneDetails getSmartphoneDetails() {
        return smartphoneDetails;
    }

    public void setSmartphoneDetails(SmartphoneDetails smartphoneDetails) {
        this.smartphoneDetails = smartphoneDetails;
    }
}
