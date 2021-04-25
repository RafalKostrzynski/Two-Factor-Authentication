package pl.kostrzynski.tfa.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kostrzynski.tfa.model.Token;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payload extends Token {

    private boolean active;
    @OneToOne
    private User user;

    public Payload(String value, boolean active, LocalDateTime expirationTime, User user) {
        super(value, expirationTime);
        this.active = active;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Payload payload = (Payload) o;
        return active == payload.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), active);
    }

    @Override
    public String toString() {
        return "Payload{" +
                "active=" + active +
                '}';
    }
}
