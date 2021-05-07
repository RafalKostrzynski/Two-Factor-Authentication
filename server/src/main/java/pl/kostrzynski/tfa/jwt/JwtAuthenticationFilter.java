package pl.kostrzynski.tfa.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.kostrzynski.tfa.model.UserRole;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = getJwtFromRequest(httpServletRequest);
        if (StringUtils.hasText(jwtToken) && jwtTokenService.validateToken(jwtToken)) {
            String username = jwtTokenService.getUsernameFromToken(jwtToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    getUsernamePasswordAuthenticationToken(httpServletRequest, jwtToken, username, userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(HttpServletRequest httpServletRequest,
                                                                                       String jwtToken, String username,
                                                                                       UserDetails userDetails) throws IOException {
        Collection<? extends GrantedAuthority> authorities = getGrantedAuthorities(jwtToken, userDetails);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        return authentication;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(String jwtToken, UserDetails userDetails) throws IOException {
        Collection<? extends GrantedAuthority> authorities;
        switch (jwtTokenService.getAuthenticationStateFromToken(jwtToken)) {
            case AUTHENTICATED:
                authorities = userDetails.getAuthorities();
                break;
            case PRE_AUTHENTICATED:
                authorities = List.of(new SimpleGrantedAuthority(UserRole.PRE_AUTHENTICATED_USER));
                break;
            case MOBILE_PRE_AUTHENTICATED:
                authorities = List.of(new SimpleGrantedAuthority(UserRole.MOBILE_PRE_AUTHENTICATED));
                break;
            case MOBILE_AUTHENTICATED:
                authorities = List.of(new SimpleGrantedAuthority(UserRole.MOBILE_AUTHENTICATED));
                break;
            case MOBILE_RESET_PASSWORD:
                authorities = List.of(new SimpleGrantedAuthority(UserRole.MOBILE_RESET_PASSWORD));
                break;
            default:
                throw new IOException("Something went wrong please try again later");
        }
        return authorities;
    }
}
