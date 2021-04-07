package pl.kostrzynski.tfa.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SecondAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "public_key", nullable = false, unique = true)
    @NotNull(message = "Please provide public key")
    private byte[] publicKeyBytes;
    @Column(nullable = false)
    private boolean active;
    @Column(unique = true)
    private String androidID;
    @OneToOne
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondAuth that = (SecondAuth) o;
        return id == that.id && active == that.active && Arrays.equals(publicKeyBytes, that.publicKeyBytes) && Objects.equals(androidID, that.androidID);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, active, androidID);
        result = 31 * result + Arrays.hashCode(publicKeyBytes);
        return result;
    }

    @Override
    public String toString() {
        return "SecondAuth{" +
                "id=" + id +
                ", publicKeyBytes=" + Arrays.toString(publicKeyBytes) +
                ", active=" + active +
                ", androidID='" + androidID + '\'' +
                '}';
    }
}
