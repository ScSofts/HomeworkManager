package edu.njust.homework_manager.backend.controllers.storages;

import edu.njust.homework_manager.backend.models.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPayload {
    public String username;
    public User.Role role;

    public TokenPayload(String username, User.Role role) {
        this.username = username;
        this.role = role;
    }
}
