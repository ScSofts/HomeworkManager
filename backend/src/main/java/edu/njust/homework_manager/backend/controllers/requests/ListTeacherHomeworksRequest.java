package edu.njust.homework_manager.backend.controllers.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record ListTeacherHomeworksRequest(
        @NotEmpty(message = "error.token.NotEmpty")
        String token,

        @NotEmpty(message = "error.username.NotEmpty")
        @Length(min = 4, max = 20, message = "error.username.Length")
        String username,

        @NotNull(message = "error.classroomId.Null")
        Long classroom_id
) {
}
