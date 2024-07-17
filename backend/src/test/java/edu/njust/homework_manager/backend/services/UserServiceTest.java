package edu.njust.homework_manager.backend.services;

import edu.njust.homework_manager.backend.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Logger log = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createUser() {
        userService.deleteUser("test");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var result = userService.createUser("test", "test", User.Role.STUDENT);
        assertNotNull(result);
        log.info("Created user: {}", result.toString());
    }

    @Test
    void queryUser() {
        createUser();
        when(userRepository.findByUsername("test")).thenReturn(User.builder()
                .username("test")
                .password("test")
                .role(User.Role.STUDENT)
                .createdAt(new java.util.Date())
                .build());
        var result = userService.queryUser("test");
        assertNotNull(result);
        log.info("Queried user: {}", result);
    }

    @Test
    void updateLoginTime() {
        createUser();
        when(userRepository.findByUsername("test")).thenReturn(User.builder()
                .username("test")
                .password("test")
                .role(User.Role.STUDENT)
                .createdAt(new java.util.Date())
                .build());
        AtomicReference<User> savedUser = new AtomicReference<>();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            savedUser.set(invocation.getArgument(0));
            return savedUser.get();
        });
        var result = userService.updateLoginTime("test");
        assertTrue(result);
        assertNotNull(savedUser.get().getLastLogin());
        log.info("Updated user: {}", result);
    }
}