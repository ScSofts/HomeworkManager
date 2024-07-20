package edu.njust.homework_manager.backend.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;




@Entity
@Data
@Builder
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long classroom_id;


    @Column(nullable = false)
    public Date created_at;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "teacher_id")
    public User teacher;


    public Classroom() {

    }

    public Classroom(Long classroom_id, Date created_at, User teacher) {
        this.classroom_id = classroom_id;
        this.created_at = created_at;
        this.teacher = teacher;
    }
}
