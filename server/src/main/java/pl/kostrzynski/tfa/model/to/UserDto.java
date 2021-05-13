package pl.kostrzynski.tfa.model.to;

import lombok.Data;
import pl.kostrzynski.tfa.model.entity.User;

@Data
public class UserDto {

    private long id;
    private String email;
    private String username;
    private String password;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = null;
    }
}
