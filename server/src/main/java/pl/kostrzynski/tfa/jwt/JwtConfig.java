package pl.kostrzynski.tfa.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
public class JwtConfig {
    @Value("${expirationTimeAuthenticatedMS}")
    private int expirationTimeAuthenticated;
    @Value("${expirationTimePreAuthenticatedMS}")
    private int expirationTimePreAuthenticated;
    @Value("${JwtSecretKey}")
    private String secret;
}
