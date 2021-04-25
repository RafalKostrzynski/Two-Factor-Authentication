package pl.kostrzynski.tfa.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    @Column(name = "public_key", nullable = false)
    @NotNull(message = "Please provide public key")
    private byte[] publicKeyBytes;
    @Column(unique = true, nullable = false)
    @JsonIgnore
    private boolean changeKey;
    @OneToOne
    @JsonIgnore
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecondAuth that = (SecondAuth) o;
        return id == that.id && changeKey == that.changeKey && Arrays.equals(publicKeyBytes, that.publicKeyBytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, changeKey);
        result = 31 * result + Arrays.hashCode(publicKeyBytes);
        return result;
    }

    @Override
    public String toString() {
        return "SecondAuth{" +
                "id=" + id +
                ", publicKeyBytes=" + Arrays.toString(publicKeyBytes) +
                ", changeKey=" + changeKey +
                '}';
    }
}
