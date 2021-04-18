package pl.kostrzynski.tfa.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    public static final String USER = "USER";
    public static final String PRE_AUTHENTICATED_USER = "PRE_AUTHENTICATED_USER";
}
