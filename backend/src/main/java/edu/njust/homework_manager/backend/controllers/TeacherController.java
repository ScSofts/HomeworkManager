package edu.njust.homework_manager.backend.controllers;

import com.google.gson.Gson;
import edu.njust.homework_manager.backend.controllers.requests.*;
import edu.njust.homework_manager.backend.controllers.storages.TokenStorage;
import edu.njust.homework_manager.backend.models.Classroom;
import edu.njust.homework_manager.backend.models.Homework;
import edu.njust.homework_manager.backend.models.Submission;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.backend.services.*;
import edu.njust.homework_manager.protocol.ApiResult;
import edu.njust.homework_manager.utils.Tuple;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    private final Gson gson;
    private final MessageSource messageSource;
    private final ClassroomService classroomService;
    private final UserService userService;
    private final HomeworkService homeworkService;
    private final SubmissionService submissionService;
    private final GradingService gradingService;

    @Value("${security.salt}")
    private String salt;

    public TeacherController(@Qualifier("gson") Gson gson, @Qualifier("messageSource") MessageSource messageSource, ClassroomService classroomService, UserService userService, HomeworkService homeworkService, SubmissionService submissionService, GradingService gradingService) {
        this.gson = gson;
        this.messageSource = messageSource;
        this.classroomService = classroomService;
        this.userService = userService;
        this.homeworkService = homeworkService;
        this.submissionService = submissionService;
        this.gradingService = gradingService;
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
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
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
        var classes = classroomService.queryClassroomByTeacher(user);
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
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
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

        var classroom = classroomService.queryClassroomById(request.classroom_id());

        if (!classroom.getTeacher().equals(user)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<Long>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.Permission", null, locale))
                            .build());
        }
        return ResponseEntity.ok(
                ApiResult.<List<Long>>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                homeworkService.queryHomeworkByClassroom(classroom)
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
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<HomeworkBrief>builder()
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
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
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


    // Create homework
    // POST /teacher/create_homework
    // Request
    // {
    //     "token": "string",
    //     "username": "string",
    //     "classroom_id": “long”,
    //     "title": "string",
    // }
    // Result: return the homework_id
    @PostMapping("/create_homework")
    public ResponseEntity<ApiResult<Long>> createHomework(
            @Valid
            @RequestBody
            CreateHomeworkRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<Long>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Long>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var classroom = classroomService.queryClassroomById(request.classroom_id());
        if (classroom == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Long>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.createHomework(classroom, request.title(), "", new Date());

        if (Objects.isNull(homework)) {
            return ResponseEntity.status(500)
                    .body(ApiResult.<Long>builder()
                            .status(500)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.Create", null, locale))
                            .build());

        }
        return ResponseEntity.ok(
                ApiResult.<Long>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(homework.getHomework_id())
                        .build()
        );
    }

    @PostMapping("/update_homework")
    public ResponseEntity<ApiResult<Boolean>> updateHomework(
            @Valid
            @RequestBody
            UpdateHomeworkRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<Boolean>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }


        var homework = homeworkService.queryHomework(request.homework_id());
        if (homework == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .build());
        }

        if (!homeworkService.updateHomework(
                request.homework_id(),
                request.title(),
                request.description(),
                request.deadline()
        )) {
            return ResponseEntity.status(500)
                    .body(ApiResult.<Boolean>builder()
                            .status(500)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.Update", null, locale))
                            .build());
        }
        return ResponseEntity.ok(
                ApiResult.<Boolean>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(true)
                        .build()
        );
    }


    // 列出对应作业提交的id
    @PostMapping("/list_homework_submission")
    public ResponseEntity<ApiResult<List<Long>>> listHomeworkSubmission(
            @Valid
            @RequestBody
            ListHomeworkSubmissionRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
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

        var homework = homeworkService.queryHomework(request.homework_id());
        if (homework == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Long>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .build());
        }


        return ResponseEntity.ok(
                ApiResult.<List<Long>>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                submissionService.querySubmissionsByHomework(homework)
                                        .stream()
                                        .map(Submission::getSubmission_id)
                                        .toList()
                        ).build()
        );
    }

    public record SubmissionBrief(
            Long submission_id,
            String username,
            Date submit_at,
            Submission.Status status
    ) {
    }

    @PostMapping("/get_submission_brief")
    public ResponseEntity<ApiResult<SubmissionBrief>> getSubmissionBrief(
            @Valid
            @RequestBody
            GetSubmissionRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<SubmissionBrief>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<SubmissionBrief>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var submission = submissionService.getSubmissionById(request.submission_id());
        if (submission == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<SubmissionBrief>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.submission.NotFound", null, locale))
                            .build());
        }

        return ResponseEntity.ok(
                ApiResult.<SubmissionBrief>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                new SubmissionBrief(
                                        submission.getSubmission_id(),
                                        submission.getStudent().getUsername(),
                                        submission.getSubmit_at(),
                                        submission.getStatus()
                                )
                        ).build()
        );
    }

    public record SubmissionDetail(
            Long submission_id,
            String username,
            Date created_at,
            String content,
            Submission.Status status
    ) {
    }

    @PostMapping("/get_submission_detail")
    public ResponseEntity<ApiResult<SubmissionDetail>> getSubmissionDetail(
            @Valid
            @RequestBody
            GetSubmissionRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<SubmissionDetail>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<SubmissionDetail>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var submission = submissionService.getSubmissionById(request.submission_id());
        if (submission == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<SubmissionDetail>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.submission.NotFound", null, locale))
                            .build());
        }

        return ResponseEntity.ok(
                ApiResult.<SubmissionDetail>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                new SubmissionDetail(
                                        submission.getSubmission_id(),
                                        submission.getStudent().getUsername(),
                                        submission.getSubmit_at(),
                                        submission.getContent(),
                                        submission.getStatus()
                                )
                        ).build()
        );
    }


    @PostMapping("/grade_submission")
    public ResponseEntity<ApiResult<Boolean>> gradeSubmission(
            @Valid
            @RequestBody
            GradeSubmissionRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<Boolean>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var submission = submissionService.getSubmissionById(request.submission_id());
        if (submission == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.submission.NotFound", null, locale))
                            .build());
        }

        var grade = gradingService.createGrading(submission,
                user,
                request.grade(),
                request.comment()
        );
        if (grade == null) {
            return ResponseEntity.status(500)
                    .body(ApiResult.<Boolean>builder()
                            .status(500)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.submission.Grade", null, locale))
                            .build());
        }
        return ResponseEntity.ok(
                ApiResult.<Boolean>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(true)
                        .build()
        );
    }


    public record ClassStatistics(
            Long homework_id,
            String title,
            List<Tuple<String,Long>> student_grades,
            Long unsubmitted_count
    ) {
    }

    @PostMapping("/get_class_statistics")
    public ResponseEntity<ApiResult<List<ClassStatistics>>> getClassStatistics(
            @Valid
            @RequestBody
            ListHomeworkRequest request,
            Locale locale
    ){
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<ClassStatistics>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<ClassStatistics>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var classroom = classroomService.queryClassroomById(request.classroom_id());
        if (classroom == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<ClassStatistics>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.NotFound", null, locale))
                            .build());
        }
        if(!classroom.getTeacher().getUserId().equals(user.getUserId())){
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<ClassStatistics>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.Permission", null, locale))
                            .build());
        }

        var homeworks = homeworkService.queryHomeworkByClassroom(classroom);
        return ResponseEntity.ok(
                ApiResult.<List<ClassStatistics>>builder()
                        .status(200)
                        .timestamp(new Date())
                        .data(
                                homeworks.stream().map(homework -> {
                                    var submissions = submissionService.querySubmissionsByHomework(homework);
                                    List<Tuple<String, Long>> student_grades = submissions.stream().map(submission -> {
                                        var grading = gradingService.getGradingBySubmission(submission);
                                        if(grading == null){
                                            return new Tuple<>(submission.getStudent().getUsername(), -1L);
                                        }
                                        return new Tuple<>(submission.getStudent().getUsername(),(long)grading.getScore());
                                    }).toList();

                                    Long unsubmitted_count = (long) (classroomService
                                                                                .getStudents(homework.classroom)
                                                                                .size() - submissions.size());
                                    return new ClassStatistics(homework.getHomework_id(),homework.getTitle(), student_grades, unsubmitted_count);
                                }).toList()
                        ).build()
        );
    }

    public record Student(
            String username,
            Long student_id,
            Date created_at,
            @Nullable
            Date last_login
    ) {

    }

    @PostMapping("/list_student")
    public ResponseEntity<ApiResult<List<Student>>> listStudent(
            @Valid
            @RequestBody
            ListStudentRequest request,
            Locale locale
    ){
        var token = request.token();
        var username = request.username();
        var classroom_id = request.classroom_id();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<Student>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if (user == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Student>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }
        var classroom = classroomService.queryClassroomById(classroom_id);
        if (classroom == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Student>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.NotFound", null, locale))
                            .build());
        }
        var students = classroomService.getStudents(classroom);
        return ResponseEntity.ok(
                ApiResult.<List<Student>>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(
                                students
                                        .stream()
                                        .map(student -> new Student(
                                                student.getUsername(),
                                                student.getUserId(),
                                                student.getCreatedAt(),
                                                student.getLastLogin()
                                        ))
                                        .toList()
                        ).build()
        );
    }




    // 返回classroom_id
    @PostMapping("/create_classroom")
    public ResponseEntity<ApiResult<Long>> createClassroom(
            @Valid
            @RequestBody
            CreateClassroomRequest request,
            Locale locale
    ){
        var token = request.token();
        var username = request.username();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<Long>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var teacher = userService.queryUser(username);
        if (teacher == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Long>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }


        var classroom = classroomService.createClassroom(teacher, List.of());
        if (classroom == null) {
            return ResponseEntity.status(500)
                    .body(ApiResult.<Long>builder()
                            .status(500)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.Create", null, locale))
                            .build());
        }
        return ResponseEntity.ok(
                ApiResult.<Long>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(classroom._1().getClassroom_id())
                        .build()
        );
    }


    @PostMapping("/remove_student")
    public ResponseEntity<ApiResult<Boolean>> removeStudent(
            @Valid
            @RequestBody
            RemoveStudentRequest request,
            Locale locale
    ){
        var token = request.token();
        var username = request.username();
        var classroom_id = request.classroom_id();
        var student_id = request.student_id();
        if (!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)) {
            return ResponseEntity.status(403)
                    .body(ApiResult.<Boolean>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var teacher = userService.queryUser(username);
        if (teacher == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var classroom = classroomService.queryClassroomById(classroom_id);
        if (classroom == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.NotFound", null, locale))
                            .build());
        }

        var student = userService.queryUser(student_id);
        if (student == null) {
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.student.NotFound", null, locale))
                            .build());
        }

        if(!classroomService.leaveClassroom(student, classroom)){
            return ResponseEntity.status(500)
                    .body(ApiResult.<Boolean>builder()
                            .status(500)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.student.Remove", null, locale))
                            .build());
        }
        return ResponseEntity.ok(
                ApiResult.<Boolean>builder()
                        .status(200)
                        .timestamp(new java.util.Date())
                        .data(true)
                        .build()
        );
    }

}
