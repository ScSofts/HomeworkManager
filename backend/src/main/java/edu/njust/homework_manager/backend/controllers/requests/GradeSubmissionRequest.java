package edu.njust.homework_manager.backend.controllers.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record GradeSubmissionRequest(
        @NotNull(message = "error.token.NotNull")
        String token,

        @NotNull(message = "error.username.NotNull")
        String username,

        @NotNull(message = "error.submission_id.NotNull")
        Long submission_id,

        @NotNull(message = "error.grade.NotNull")
        @Min(value = 0, message = "error.grade.Min")
        @Max(value = 100, message = "error.grade.Max")
        Integer grade,

        @NotNull(message = "error.comment.NotNull")
        String comment
) {
}
