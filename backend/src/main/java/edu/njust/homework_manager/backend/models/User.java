package edu.njust.homework_manager.backend.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long userId;

    @Column(unique = true, nullable = false)
    public String username;

    @Column(nullable = false)
    public String password;

//    public String email;

    public enum Role{
        STUDENT,
        TEACHER,
//        ADMINISTRATOR
    }

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public Role role;


    @Column(nullable = false)
    public Date createdAt;

    @Column(nullable = true)
    public Date lastLogin;

    public User() { }


    public User(Long user_id, String user_name, String password, Role role, Date created_at, Date last_login) {
        this.userId = user_id;
        this.username = user_name;
        this.password = password;
        this.role = role;
        this.createdAt = created_at;
        this.lastLogin = last_login;
    }

}
