package pl.kostrzynski.tfa.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kostrzynski.tfa.model.enums.AuthenticationState;

import java.util.Date;

@Service
public class JwtTokenService {

    private final JwtConfig jwtConfig;
    private static final String AUTHENTICATION_STATE = "AuthenticationState";

    public JwtTokenService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String createToken(Authentication authentication, AuthenticationState authenticationState) {
        Date expirationDate = new Date(new Date().getTime() + (
                authenticationState == AuthenticationState.AUTHENTICATED  ?
                jwtConfig.getExpirationTimeAuthenticated() :
                jwtConfig.getExpirationTimePreAuthenticated()));

        return Jwts.builder().setSubject(authentication.getName())
                .claim(AUTHENTICATION_STATE, authenticationState)
                .setIssuedAt(new Date()).setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();
    }

    private Claims getClaimsFromJwt(String jwtToken) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret().getBytes())
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public String getUsernameFromToken(String jwtToken) {
        Claims claims = getClaimsFromJwt(jwtToken);
        return claims.getSubject();
    }

    public AuthenticationState getAuthenticationStateFromToken(String jwtToken) {
        Claims claims = getClaimsFromJwt(jwtToken);
        String authenticationState = claims.get("AuthenticationState", String.class);
        return AuthenticationState.valueOf(authenticationState);
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
