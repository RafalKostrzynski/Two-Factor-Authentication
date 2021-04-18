package pl.kostrzynski.tfa.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtConfig jwtConfig;

    public JwtTokenService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public Claims getClaimsFromJWT(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String jwtToken){
        try {
            Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(jwtToken);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
