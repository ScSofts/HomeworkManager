package edu.njust.homework_manager.backend.controllers;

import com.google.gson.Gson;
import edu.njust.homework_manager.backend.controllers.requests.GetHomeworkRequest;
import edu.njust.homework_manager.backend.controllers.requests.ListClassroomRequest;
import edu.njust.homework_manager.backend.controllers.requests.ListHomeworkRequest;
import edu.njust.homework_manager.backend.controllers.requests.SubmitHomeworkRequest;
import edu.njust.homework_manager.backend.controllers.storages.TokenStorage;
import edu.njust.homework_manager.backend.models.Classroom;
import edu.njust.homework_manager.backend.models.Homework;
import edu.njust.homework_manager.backend.models.Submission;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.backend.services.ClassroomService;
import edu.njust.homework_manager.backend.services.HomeworkService;
import edu.njust.homework_manager.backend.services.SubmissionService;
import edu.njust.homework_manager.backend.services.UserService;
import edu.njust.homework_manager.protocol.ApiResult;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final MessageSource messageSource;
    private final Gson gson;
    private final UserService userService;
    private final ClassroomService classroomService;
    private final HomeworkService homeworkService;
    private final SubmissionService submissionService;

    @Value("${security.salt}")
    private String salt;

    public StudentController(@Qualifier("messageSource") MessageSource messageSource, @Qualifier("gson") Gson gson, UserService userService, ClassroomService classroomService, HomeworkService homeworkService, SubmissionService submissionService) {
        this.messageSource = messageSource;
        this.gson = gson;
        this.userService = userService;
        this.classroomService = classroomService;
        this.homeworkService = homeworkService;
        this.submissionService = submissionService;
    }

    @PostMapping("/list_classroom")
    public ResponseEntity<ApiResult<List<Long>>> listClassroom(
            @Valid
            @RequestBody
            ListClassroomRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.STUDENT, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<Long>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Long>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }
        var classes = classroomService.queryClassroomByStudent(user);
        return ResponseEntity.ok(
                ApiResult.<List<Long>>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                classes
                                        .stream()
                                        .map(Classroom::getClassroom_id)
                                        .toList()
                        ).build()
        );
    }


    @PostMapping("/list_homework")
    public ResponseEntity<ApiResult<List<Long>>> listHomework(
            @Valid
            @RequestBody
            ListHomeworkRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        var classroom_id = request.classroom_id();
        if (!TokenStorage.verify(username, User.Role.STUDENT, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<Long>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Long>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }
        var classroom = classroomService.queryClassroomById(classroom_id);
        if (classroom == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Long>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.NotFound", null, locale))
                            .build());
        }
        var homeworks = homeworkService.queryHomeworkByClassroom(classroom);
        return ResponseEntity.ok(
                ApiResult.<List<Long>>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                homeworks
                                        .stream()
                                        .map(Homework::getHomework_id)
                                        .toList()
                        ).build()
        );
    }


    public record HomeworkBrief(
            Long homework_id,
            String title,
            Date deadline,
            Date created_at
    ) {
    }

    @PostMapping("/get_homework_brief")
    public ResponseEntity<ApiResult<HomeworkBrief>> getHomeworkTitle(
            @Valid
            @RequestBody
            GetHomeworkRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.STUDENT, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<StudentController.HomeworkBrief>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<HomeworkBrief>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.queryHomework(request.homework_id());
        if (homework == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<HomeworkBrief>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .build());
        }

        return ResponseEntity.ok(
                ApiResult.<HomeworkBrief>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                new HomeworkBrief(
                                        homework.getHomework_id(),
                                        homework.getTitle(),
                                        homework.getDeadline(),
                                        homework.getCreated_at()
                                )
                        ).build()
        );
    }

    public record HomeworkDetail(
            Long homework_id,
            String title,
            Date deadline,
            Date created_at,
            String description
    ) {
    }

    @PostMapping("/get_homework_detail")
    public ResponseEntity<ApiResult<HomeworkDetail>> getHomeworkDetail(
            @Valid
            @RequestBody
            GetHomeworkRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.STUDENT, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<HomeworkDetail>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<HomeworkDetail>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.queryHomework(request.homework_id());
        if (homework == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<HomeworkDetail>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .build());
        }

        return ResponseEntity.ok(
                ApiResult.<HomeworkDetail>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                new HomeworkDetail(
                                        homework.getHomework_id(),
                                        homework.getTitle(),
                                        homework.getDeadline(),
                                        homework.getCreated_at(),
                                        homework.getDescription()
                                )
                        ).build()
        );
    }

    @PostMapping("/submit_homework")
    public ResponseEntity<ApiResult<Boolean>> submitHomework(
            @Valid
            @RequestBody
            SubmitHomeworkRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.STUDENT, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<Boolean>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .data(false)
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .data(false)
                            .build());
        }

        var homework = homeworkService.queryHomework(request.homework_id());
        if (homework == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .data(false)
                            .build());
        }


        if(homework.getDeadline().before(new Date())){
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.submit.Deadline", null, locale))
                            .data(false)
                            .build());
        }

        var last_submit = submissionService
                .querySubmissionsByStudent(user)
                .stream()
                .filter(submission -> {
                    return Objects.equals(submission.homework.getHomework_id(), homework.getHomework_id());
                }).findFirst();

        Submission result;
        if(last_submit.isPresent()){
            result = last_submit.get();
            if(result.getStatus()  == Submission.Status.ACCEPTED || !submissionService.updateSubmission(result.getSubmission_id(), request.content())){
                return ResponseEntity.status(400)
                        .body(ApiResult.<Boolean>builder()
                                .status(400)
                                .timestamp(new java.util.Date())
                                .error(messageSource.getMessage("error.submit.UpdateFailed", null, locale))
                                .data(false)
                                .build());
            }
        }else{
            result = submissionService.createSubmission(homework, user, request.content());
        }

        if (result != null) {
            return ResponseEntity.ok(
                    ApiResult.<Boolean>builder()
                            .status(200)
                            .timestamp(new java.util.Date())
                            .data(true)
                            .build()
            );
        } else {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.submit.Failed", null, locale))
                            .data(false)
                            .build());
        }
    }

    public record SubmissionStatus(
            Boolean submitted,
            Long submission_id,
            Long homework_id,
            String content,
            Submission.Status status,
            Date submit_at
    ) {
    }

    @PostMapping("/check_submission")
    public ResponseEntity<ApiResult<SubmissionStatus>> checkSubmission(
            @Valid
            @RequestBody
            GetHomeworkRequest request,
            Locale locale
    ){
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.STUDENT, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<SubmissionStatus>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<SubmissionStatus>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.queryHomework(request.homework_id());
        if (homework == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<SubmissionStatus>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .build());
        }

        var submission = submissionService.getSubmission(homework, user);
        if(submission == null){
            return ResponseEntity.ok(
                    ApiResult.<SubmissionStatus>builder()
                            .status(200)
                            .timestamp(new java.util.Date())
                            .data(new SubmissionStatus(
                                    false,
                                    null,
                                    homework.getHomework_id(),
                                    null,
                                    null,
                                    null
                            ))
                            .build()
            );
        }else{
            return ResponseEntity.ok(
                    ApiResult.<SubmissionStatus>builder()
                            .status(200)
                            .timestamp(new java.util.Date())
                            .data(new SubmissionStatus(
                                    true,
                                    submission.getSubmission_id(),
                                    homework.getHomework_id(),
                                    submission.getContent(),
                                    submission.getStatus(),
                                    submission.getSubmit_at()
                            ))
                            .build()
            );
        }
    }
}
