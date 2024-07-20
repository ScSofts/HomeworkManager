package edu.njust.homework_manager.backend.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class ClassroomStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long classroom_record_id;


    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "classroom_id")
    public Classroom classroom;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "student_id")
    public User student;

    public ClassroomStudents() {
    }

    public ClassroomStudents(Long classroom_record_id, Classroom classroom, User student_id) {
        this.classroom_record_id = classroom_record_id;
        this.classroom = classroom;
        this.student = student_id;
    }

}
