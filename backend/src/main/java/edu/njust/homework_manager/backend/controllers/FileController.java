package edu.njust.homework_manager.backend.controllers;

import com.google.gson.Gson;
import edu.njust.homework_manager.backend.controllers.storages.TokenStorage;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.backend.services.UserService;
import edu.njust.homework_manager.protocol.ApiResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/files")
public class FileController {
    public static File path = new File("data/files/");
    private final Gson gson;
    @Value("${security.salt}")
    private String salt;

    public FileController(UserService userService, @Qualifier("gson") Gson gson) {
        this.gson = gson;
    }

    @GetMapping("/homework_{id}.png")
    public byte[] getHomeworkImageFile(@PathVariable("id") String id) {
        File file = new File(path, "homework_" + id + ".png");
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/submit_{id}.png")
    public byte[] getSubmitImageFile(@PathVariable("id") String id) {
        File file = new File(path, "submit_" + id + ".png");
        if (!file.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/upload")
    public ResponseEntity<ApiResult<Boolean>> uploadFile(
            @RequestBody byte[] data,
            @RequestHeader("Token") String token,
            @RequestHeader("Username") String username,
            @RequestHeader("Role")
            @Valid
            @NotEmpty(message = "error.role.NotEmpty")
            @Pattern(regexp = "^STUDENT|TEACHER$", message = "error.role.Pattern")
            String role,
            @RequestHeader("Id")
            String homework_id
    ) {

        var roleEnum = role.equals("STUDENT") ? User.Role.STUDENT : User.Role.TEACHER;
        if(!TokenStorage.verify(username, roleEnum, token, gson, salt)){
            return ResponseEntity
                    .status(403)
                    .body(
                            ApiResult
                                    .<Boolean>builder()
                                    .status(403)
                                    .data(false)
                                    .error("error.login.Expired")
                                    .build()
                    );
        }

        var prefix = switch (roleEnum) {
            case STUDENT -> "submit_";
            case TEACHER -> "homework_" + username + "_";
        };

        File file = new File(path, prefix + homework_id + ".png");
        try {
            var image = ImageIO.read(new ByteArrayInputStream(data));
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            return ResponseEntity
                    .status(500)
                    .body(
                            ApiResult
                                    .<Boolean>builder()
                                    .status(500)
                                    .data(false)
                                    .error("error.file.Upload")
                                    .build()
                    );
        }
        return ResponseEntity.ok(
                ApiResult
                        .<Boolean>builder()
                        .status(200)
                        .data(true)
                        .build()
        );
    }
}
