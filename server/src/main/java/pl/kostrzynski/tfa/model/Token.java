package pl.kostrzynski.tfa.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalTime;

@MappedSuperclass
@NoArgsConstructor
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String value;
    private LocalTime expirationTime;

    public Token(String value, LocalTime expirationTime) {
        this.value = value;
        this.expirationTime = expirationTime;
    }

    public boolean tokenNotExpired() {
        return expirationTime.isAfter(LocalTime.now());
    }
}
