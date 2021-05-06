package pl.kostrzynski.twofactorauthentication.model;

import android.os.Build;
import lombok.Data;

import java.util.Objects;

@Data
public class SmartphoneDetails {
    private String androidID;
    private String manufacturer;
    private String brand;
    private String type;

    public SmartphoneDetails(String androidID) {
        this.androidID = androidID;
        this.manufacturer = Build.MANUFACTURER;
        this.brand = Build.BRAND;
        this.type = Build.TYPE;
    }

    public String getSmartphoneDetails(){
        return getAndroidID()+getManufacturer()+getBrand()+getType();
    }
}
