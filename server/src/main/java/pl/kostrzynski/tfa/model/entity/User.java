package pl.kostrzynski.tfa.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.kostrzynski.tfa.model.UserRole;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Email(message = "Please provide a valid Email address", regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @Column(nullable = false, unique = true)
    private String email;
    @Size(min = 5, max = 30, message = "Please provide a valid username")
    @Column(nullable = false, unique = true)
    private String username;
    @Size(min = 9, max = 60, message = "Please provide a valid password")
    @Column(nullable = false)
    private String password;
    @JsonIgnore
    private String jwt;
    @JsonIgnore
    @Column(nullable = false)
    private boolean emailVerified;
    @JsonIgnore
    @Column(nullable = false)
    private boolean isEnabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && emailVerified == user.emailVerified && isEnabled == user.isEnabled && email.equals(user.email) && username.equals(user.username) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, password, emailVerified, isEnabled);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(UserRole.USER));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
