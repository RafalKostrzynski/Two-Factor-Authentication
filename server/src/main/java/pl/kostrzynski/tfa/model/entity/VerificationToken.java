package pl.kostrzynski.tfa.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kostrzynski.tfa.model.Token;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends Token {

    @OneToOne
    private User user;

    public VerificationToken(User user, String value) {
        super(value, LocalTime.now().plusHours(24));
        this.user = user;
    }
}
