package pl.kostrzynski.tfa.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kostrzynski.tfa.model.Token;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SecondAuthToken extends Token {

    @OneToOne
    private SecondAuth secondAuth;

    public SecondAuthToken(String value, SecondAuth secondAuth) {
        super(value,LocalTime.now().plusMinutes(15));
        this.secondAuth = secondAuth;
    }

}
