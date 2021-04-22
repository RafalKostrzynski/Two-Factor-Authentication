package pl.kostrzynski.tfa.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenService {

    private final JwtConfig jwtConfig;
    private static final String AUTHENTICATED = "authenticated";

    public JwtTokenService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String createToken(Authentication authentication, boolean authenticated) {
        Date expirationDate = new Date(new Date().getTime() + (authenticated ?
                jwtConfig.getExpirationTimeAuthenticated() :
                jwtConfig.getExpirationTimePreAuthenticated()));

        return Jwts.builder().setSubject(authentication.getName()).claim(AUTHENTICATED, authenticated)
                .setIssuedAt(new Date()).setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();
    }

    public Claims getClaimsFromJWT(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(jwtConfig.getSecret().getBytes()).parseClaimsJws(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
