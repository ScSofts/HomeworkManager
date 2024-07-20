package edu.njust.homework_manager.backend.controllers.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record GetSubmissionRequest(
        @NotEmpty(message = "error.username.NotEmpty")
        @Length(min = 4, max = 20, message = "error.username.Length")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "error.username.Pattern")
        String username,

        @NotEmpty(message = "error.token.NotEmpty")
        String token,

        @NotNull(message = "error.submission_id.NotNull")
        Long submission_id
) {
}
