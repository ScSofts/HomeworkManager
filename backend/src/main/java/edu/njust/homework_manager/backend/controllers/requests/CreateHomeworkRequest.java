package edu.njust.homework_manager.backend.controllers.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

public record CreateHomeworkRequest(
        @NotEmpty(message = "error.username.NotEmpty")
        @Length(min = 4, max = 20, message = "error.username.Length")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "error.username.Pattern")
        String username,

        @NotEmpty(message = "error.token.NotEmpty")
        String token,

        @NotEmpty(message = "error.title.NotEmpty")
        String title,

        @NotNull(message = "error.classroomId.NotNull")
        Long classroom_id

) {
}
