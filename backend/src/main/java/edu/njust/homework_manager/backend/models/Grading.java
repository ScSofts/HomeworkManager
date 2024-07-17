package edu.njust.homework_manager.backend.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Builder
public class Grading {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    public Long grading_id;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "submission_id")
    public Submission submission;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    public User teacher;

    @Column(nullable = false)
    public int score;

    @Column(columnDefinition = "TEXT")
    public String comment;

    public Date graded_at;

    public Grading(Long grading_id, Submission submission, User teacher, int score, String comment, Date graded_at) {
        this.grading_id = grading_id;
        this.submission = submission;
        this.teacher = teacher;
        this.score = score;
        this.comment = comment;
        this.graded_at = graded_at;
    }

    public Grading() {

    }
}
