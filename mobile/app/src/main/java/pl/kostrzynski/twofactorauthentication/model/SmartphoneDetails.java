package pl.kostrzynski.twofactorauthentication.model;

import android.os.Build;

import java.util.Objects;

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

    public String getAndroidID() {
        return androidID;
    }

    public void setAndroidID(String androidID) {
        this.androidID = androidID;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartphoneDetails that = (SmartphoneDetails) o;
        return Objects.equals(androidID, that.androidID) && Objects.equals(manufacturer, that.manufacturer) &&
                Objects.equals(brand, that.brand) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(androidID, manufacturer, brand, type);
    }

    @Override
    public String toString() {
        return "SmartphoneDetails{" +
                "androidID='" + androidID + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
