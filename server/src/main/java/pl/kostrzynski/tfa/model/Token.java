package pl.kostrzynski.tfa.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String value;
    private LocalDateTime expirationTime;

    public Token(String value, LocalDateTime expirationTime) {
        this.value = value;
        this.expirationTime = expirationTime;
    }

    public boolean isNotExpired() {
        return expirationTime.isAfter(LocalDateTime.now());
    }
}
