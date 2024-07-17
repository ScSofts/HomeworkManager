package edu.njust.homework_manager.backend.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
public class Submission {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    public Long submission_id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "homework_id")
    public Homework homework;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "student_id")
    public User student;

    @Column(columnDefinition = "TEXT")
    public String content;

    public Date submit_at;

    public Submission() {

    }



    public enum Status{
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    public Status status;

    public Submission(Long submission_id, Homework homework, User student, String content, Date submit_at, Status status) {
        this.submission_id = submission_id;
        this.homework = homework;
        this.student = student;
        this.content = content;
        this.submit_at = submit_at;
        this.status = status;
    }
}
