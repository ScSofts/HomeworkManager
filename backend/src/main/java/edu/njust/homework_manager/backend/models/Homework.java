package edu.njust.homework_manager.backend.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
public class Homework {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    public Long homework_id;

    public String title;

    @Column(columnDefinition = "TEXT")
    public String description;

    public Date created_at;

    public Date deadline;

    @ManyToOne(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "teacher_id")
    public User teacher;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "classroom_id")
    public Classroom classroom;

    public Homework(Long homework_id, String title, String description, Date created_at, Date deadline, User teacher, Classroom classroom) {
        this.homework_id = homework_id;
        this.title = title;
        this.description = description;
        this.created_at = created_at;
        this.deadline = deadline;
        this.teacher = teacher;
        this.classroom = classroom;
    }

    public Homework() { }
}
