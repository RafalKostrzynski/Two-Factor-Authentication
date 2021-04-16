package pl.kostrzynski.tfa.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
    // TODO implement time validation for the token to expire

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String value;
    private LocalTime expirationTime;
    @OneToOne
    private User user;

    public VerificationToken(User user, String value) {
        this.user = user;
        this.value = value;
        this.expirationTime = LocalTime.now().plusHours(24);
    }

    public boolean tokenNotExpired() {
        return expirationTime.isBefore(LocalTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationToken that = (VerificationToken) o;
        return id == that.id && Objects.equals(value, that.value) && Objects.equals(expirationTime, that.expirationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, expirationTime);
    }

    @Override
    public String toString() {
        return "VerificationToken{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", expirationTime=" + expirationTime +
                '}';
    }
}
