package pl.kostrzynski.tfa.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SmartphoneDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "Please provide the androidID")
    private String androidID;
    @NotNull(message = "Please provide the manufacturer")
    private String manufacturer;
    @NotNull(message = "Please provide the brand")
    private String brand;
    @NotNull(message = "Please provide the type")
    private String type;
    @OneToOne
    private SecondAuth secondAuth;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartphoneDetails that = (SmartphoneDetails) o;
        return id == that.id && androidID.equals(that.androidID) && manufacturer.equals(that.manufacturer) && brand.equals(that.brand) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, androidID, manufacturer, brand, type);
    }

    @Override
    public String toString() {
        return "SmartphoneDetails{" +
                "id=" + id +
                ", androidID='" + androidID + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
