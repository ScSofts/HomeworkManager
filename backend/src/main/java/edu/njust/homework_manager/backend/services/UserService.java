package edu.njust.homework_manager.backend.services;

import edu.njust.homework_manager.backend.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;


@Repository
interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    void deleteByUsername(String username);

}

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    private UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Nullable
    public User queryUser(String username) {
        try {
            return userRepository.findByUsername(username);
        }catch (Exception e){
            log.warn("Failed to query user by username: {}, reason: {}", username, e.getMessage());
            return null;
        }
    }

    @Nullable
    public User createUser(String username, String password, User.Role role) {
        var user = User.builder()
                .username(username)
                .password(password)
                .role(role)
                .createdAt(new Date())
                .build();
        if(queryUser(username) != null) {
            return null;
        }else {
            try {
                return userRepository.save(user);
            }catch (Exception e) {
                log.warn("Failed to create user: {}, reason: {}", user, e.getMessage());
                return null;
            }
        }
    }

    public boolean deleteUser(String username) {
        try {
            userRepository.deleteByUsername(username);
            return true;
        }catch (Exception e) {
            log.warn("Failed to delete user by username: {}, reason: {}", username, e.getMessage());
            return false;
        }
    }

    public boolean updateLoginTime(String username) {
        var user = queryUser(username);
        if(user == null) {
            return false;
        }
        user.setLastLogin(new Date());
        try {
            userRepository.save(user);
            return true;
        }catch (Exception e) {
            log.warn("Failed to update login time for user: {}, reason: {}", user, e.getMessage());
            return false;
        }
    }


}
