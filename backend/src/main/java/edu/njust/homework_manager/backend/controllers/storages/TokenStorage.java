package edu.njust.homework_manager.backend.controllers.storages;

import ch.qos.logback.core.util.TimeUtil;
import cn.hutool.core.date.DateTime;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import edu.njust.homework_manager.backend.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class TokenStorage {


    protected static final Map<String, String> tokens = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(TokenStorage.class);

    public static String createToken(String username, User.Role role, Gson gson, String salt){

        var payload = TokenPayload.builder()
                .username(username)
                .role(role)
                .build();

        var calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, +1);
        var token = JWT.create()
                .withIssuer("homework_manager")
                .withExpiresAt(calendar.getTime())
                .withPayload(gson.toJson(payload))
                .sign(Algorithm.HMAC256(salt));

        synchronized (tokens){
            tokens.put(username, token);
        }

        return token;
    }

    public static boolean verify(String username, User.Role role, String token, Gson gson, String salt){
        try {
            JWTVerifier verifier = JWT
                    .require(Algorithm.HMAC256(salt))
                    .withIssuer("homework_manager")
                    .build();
            var decode = verifier.verify(token);

            var payload = gson.fromJson(
                    new String(Base64.getDecoder().decode(
                            decode.getPayload()
                    )),
                    TokenPayload.class
            );
            if(!Objects.equals(payload.getUsername(), username))
                return false;

            if(!Objects.equals(payload.getRole(), role))
                return false;

        }catch (Exception e){
            log.warn("Decode token {} failed!", token);
            return  false;
        }
        synchronized (tokens){
            return Objects.equals(tokens.get(username), token);
        }
    }
}
