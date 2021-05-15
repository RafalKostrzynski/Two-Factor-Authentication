package pl.kostrzynski.tfa.config.webSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.kostrzynski.tfa.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().
                authorizeRequests()
                .antMatchers("/tfa/service/rest/v1/first-auth/**").permitAll()
                .antMatchers("/tfa/service/rest/v1/for-user/pub-key/update",
                        "/tfa/service/rest/v1/for-user/pub-key/update/request").hasRole("MOBILE_AUTHENTICATED")
                .antMatchers("/tfa/service/rest/v1/for-user/**").hasRole("USER")
                .antMatchers("/tfa/service/rest/v1/second-auth/verify").hasRole("MOBILE_PRE_AUTHENTICATED")
                .antMatchers("/tfa/service/rest/v1/second-auth/authenticate").hasRole("PRE_AUTHENTICATED_USER")
                .antMatchers("/tfa/service/rest/v1/second-auth/change-password").hasRole("MOBILE_RESET_PASSWORD")
                .antMatchers(HttpMethod.POST, "/tfa/service/rest/v1/second-auth/{token}").permitAll()
                .anyRequest().authenticated()
                .and()
                .requiresChannel().anyRequest().requiresSecure();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // session policy is set to stateless so cookies are not in use, therefore csrf is not needed
        http.csrf().disable();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
