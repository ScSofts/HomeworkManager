package edu.njust.homework_manager.backend.controllers;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.gson.Gson;
import edu.njust.homework_manager.backend.controllers.requests.UserLoginRequest;
import edu.njust.homework_manager.backend.controllers.requests.UserRegisterRequest;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.backend.services.UserService;
import edu.njust.homework_manager.protocol.ApiResult;
import edu.njust.homework_manager.backend.controllers.storages.TokenStorage;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final MessageSource messageSource;
    private final Gson gson;
    @Value("${security.salt}")
    private String salt;


    public UserController(UserService userService, @Qualifier("messageSource") MessageSource messageSource, @Qualifier("gson") Gson gson) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.gson = gson;
    }


    public record UserLoginResponse(
            String token
    ) {
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<UserLoginResponse>> login(
            @Valid
            @RequestBody
            UserLoginRequest request,
            Locale locale
    ) throws NoSuchAlgorithmException {

        ApiResult<UserLoginResponse> result = null;
        log.info("User login: {}", request);
        var token = getCodeToken(request.captcha());
        if (!request.token().equals(token)) {
            result = ApiResult.<UserLoginResponse>builder()
                    .status(400)
                    .error(getErrorMessage("error.captcha.Incorrect", locale))
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(result);
        }

        var user = userService.queryUser(request.username());

        if (user == null) {
            result = ApiResult.<UserLoginResponse>builder()
                    .status(400)
                    .error(getErrorMessage("error.username.NotFound", locale))
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(result);
        }

        if (!user.getPassword().equals(request.password())) {
            result = ApiResult.<UserLoginResponse>builder()
                    .status(400)
                    .error(getErrorMessage("error.password.Incorrect", locale))
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(result);
        }

        var role = request.role().equals("STUDENT") ? User.Role.STUDENT : User.Role.TEACHER;
        if (user.getRole() != role) {
            result = ApiResult.<UserLoginResponse>builder()
                    .status(400)
                    .error(getErrorMessage("error.role.Incorrect", locale))
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(result);
        }

        userService.updateLoginTime(user.getUsername());

        var jwt_token = TokenStorage.createToken(
                user.getUsername(),
                user.getRole(),
                gson,
                salt
        );
        result = ApiResult.<UserLoginResponse>builder()
                .status(200)
                .data(new UserLoginResponse(jwt_token))
                .build();
        return ResponseEntity
                .ok()
                .body(result);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResult<Boolean>> register(
            @Valid
            @RequestBody
            UserRegisterRequest user,
            Locale locale
    ) throws NoSuchAlgorithmException {

        var token = getCodeToken(user.captcha());
        if (!user.token().equals(token)) {
            var result = ApiResult.<Boolean>builder()
                    .status(400)
                    .timestamp(new Date())
                    .data(false)
                    .error(getErrorMessage("error.captcha.Incorrect", locale))
                    .build();

            return ResponseEntity
                    .status(result.getStatus())
                    .body(result);
        }

        var role = user.role().equals("STUDENT") ? User.Role.STUDENT : User.Role.TEACHER;
        var newUser = userService.createUser(user.username(), user.password(), role);

        if (newUser == null) {
            var result = ApiResult.<Boolean>builder()
                    .status(400)
                    .timestamp(new Date())
                    .data(false)
                    .error(getErrorMessage("error.username.Exists", locale))
                    .build();
            return ResponseEntity
                    .status(result.getStatus())
                    .body(result);
        }

        var result = ApiResult.<Boolean>builder()
                .status(200)
                .timestamp(new Date())
                .data(true)
                .build();
        return ResponseEntity
                .status(result.getStatus())
                .body(result);
    }


    public record CaptchaResult(
            String image,
            String token
    ) {
    }

    @PostMapping("/captcha")
    public ApiResult<CaptchaResult> captcha() throws NoSuchAlgorithmException {
        var captcha = CaptchaUtil.createCircleCaptcha(120, 32, 5, 4);
        var image = captcha.getImageBase64();
        var code = captcha.getCode();

        log.info("Captcha code: {}", code);

        var token = getCodeToken(code);

        return ApiResult.<CaptchaResult>builder()
                .status(200)
                .data(new CaptchaResult(image, token))
                .build();
    }

    private String getCodeToken(String code) throws NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance("SHA-256");

        var token_raw = ArrayUtil.addAll(
                code.toLowerCase().getBytes(),
                salt.getBytes(),
                Long.toString(new Date().getTime() / 60 / 1000).getBytes()
        );
        var token_hash = digest.digest(token_raw);
        return new String(Base64.getEncoder()
                .encode(token_hash)
        );
    }

    private String getErrorMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }
}
