package edu.njust.homework_manager.backend.controllers.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record ListHomeworkSubmissionRequest(
        @NotNull(message = "error.homeworkId.NotNull")
        Long homework_id,

        @NotEmpty(message = "error.token.NotEmpty")
        String token,

        @NotEmpty(message = "error.username.NotEmpty")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "error.username.Pattern")
        @Length(min = 4, max = 20, message = "error.username.Length")
        String username
) {
}