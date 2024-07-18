package edu.njust.homework_manager.backend.controllers;

import com.google.gson.Gson;
import edu.njust.homework_manager.backend.controllers.requests.*;
import edu.njust.homework_manager.backend.controllers.storages.TokenStorage;
import edu.njust.homework_manager.backend.models.Classroom;
import edu.njust.homework_manager.backend.models.Homework;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.backend.services.ClassroomService;
import edu.njust.homework_manager.backend.services.HomeworkService;
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
@RequestMapping("/teacher")
public class TeacherController {
    private final Gson gson;
    private final MessageSource messageSource;
    private final ClassroomService classroomService;
    private final UserService userService;
    private final HomeworkService homeworkService;

    @Value("${security.salt}")
    private String salt;

    public TeacherController(@Qualifier("gson") Gson gson, @Qualifier("messageSource") MessageSource messageSource, ClassroomService classroomService, UserService userService, HomeworkService homeworkService) {
        this.gson = gson;
        this.messageSource = messageSource;
        this.classroomService = classroomService;
        this.userService = userService;
        this.homeworkService = homeworkService;
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
        if(!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)){
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<Long>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if(user == null){
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
            ListTeacherHomeworksRequest request,
            Locale locale
    ) {
        var token = request.token();
        var username = request.username();
        if(!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)){
            return ResponseEntity.status(403)
                    .body(ApiResult.<List<Long>>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if(user == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<List<Long>>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var classroom = classroomService.queryClassroomById(request.classroom_id());

        if(!classroom.getTeacher().equals(user)){
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
        if(!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)){
            return ResponseEntity.status(403)
                    .body(ApiResult.<HomeworkBrief>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if(user == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<HomeworkBrief>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.queryHomework(request.homework_id());
        if(homework == null){
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
        if(!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)){
            return ResponseEntity.status(403)
                    .body(ApiResult.<HomeworkDetail>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if(user == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<HomeworkDetail>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.queryHomework(request.homework_id());
        if(homework == null){
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
        if(!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)){
            return ResponseEntity.status(403)
                    .body(ApiResult.<Long>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if(user == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<Long>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }

        var classroom = classroomService.queryClassroomById(request.classroom_id());
        if(classroom == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<Long>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.classroom.NotFound", null, locale))
                            .build());
        }

        var homework = homeworkService.createHomework(classroom, request.title(), "", new Date());

        if(Objects.isNull(homework)){
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
        if(!TokenStorage.verify(username, User.Role.TEACHER, token, gson, salt)){
            return ResponseEntity.status(403)
                    .body(ApiResult.<Boolean>builder()
                            .status(403)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.login.Expired", null, locale))
                            .build());
        }

        var user = userService.queryUser(username);
        if(user == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.user.NotFound", null, locale))
                            .build());
        }



        var homework = homeworkService.queryHomework(request.homework_id());
        if(homework == null){
            return ResponseEntity.status(400)
                    .body(ApiResult.<Boolean>builder()
                            .status(400)
                            .timestamp(new java.util.Date())
                            .error(messageSource.getMessage("error.homework.NotFound", null, locale))
                            .build());
        }

        if(!homeworkService.updateHomework(
                request.homework_id(),
                request.title(),
                request.description(),
                request.deadline()
        )){
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


}
