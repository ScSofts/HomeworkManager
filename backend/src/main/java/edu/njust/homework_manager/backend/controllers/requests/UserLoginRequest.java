package edu.njust.homework_manager.backend.controllers.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record UserLoginRequest(
        @NotEmpty(message = "{error.username.NotEmpty}")
        @Length(min = 4, max = 20, message = "{error.username.Length}")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "{error.username.Pattern}")
        String username,

        @NotEmpty(message = "{error.password.NotEmpty}")
        @Length(min = 8, max = 28, message = "{error.password.Length}")
        @Pattern(regexp = "^[a-zA-Z0-9_~!@#$%^&*()-=+]+$", message = "{error.password.Pattern}")
        String password,

        @NotEmpty(message = "{error.captcha.NotEmpty}")
        @Length(min = 5, max = 5, message = "{error.captcha.Length}")
        String captcha,

        @NotEmpty(message = "{error.token.NotEmpty}")
        String token,

        @NotEmpty(message = "{error.role.NotEmpty}")
        @Pattern(regexp = "^(STUDENT|TEACHER)$", message = "{error.role.Pattern}")
        String role
) {
}