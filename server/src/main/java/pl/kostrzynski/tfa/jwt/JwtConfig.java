package pl.kostrzynski.tfa.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class JwtConfig {
    @Value("${JwtSecretKey}")
    private int expirationTime;
    @Value("${expirationTimeMS}")
    private String secret;
}
