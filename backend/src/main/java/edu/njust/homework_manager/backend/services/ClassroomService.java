package edu.njust.homework_manager.backend.services;

import edu.njust.homework_manager.backend.models.Classroom;
import edu.njust.homework_manager.backend.models.ClassroomStudents;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.utils.Tuple;
import org.antlr.v4.runtime.misc.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Repository
interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findAllByTeacher(User teacher);
}

@Repository
interface ClassroomStudentsRepository extends JpaRepository<ClassroomStudents, Long> {
    void deleteByClassroom(Classroom classroom);
    List<ClassroomStudents> findAllByClassroom(Classroom classroom);
    List<ClassroomStudents> findAllByStudent(User student);
}


@Service
public class ClassroomService {

    private static final Logger log = LoggerFactory.getLogger(ClassroomService.class);
    private final ClassroomRepository classroomRepository;
    private final ClassroomStudentsRepository classroomStudentsRepository;

    @Autowired
    private ClassroomService(ClassroomRepository classroomRepository, ClassroomStudentsRepository classroomStudentsRepository) {
        this.classroomRepository = classroomRepository;
        this.classroomStudentsRepository = classroomStudentsRepository;
    }

    @Nullable
    public Tuple<Classroom, List<ClassroomStudents>> createClassroom(User teacher, List<User> students) {
        try {
            var classroom = Classroom.builder()
                    .teacher(teacher)
                    .created_at(new Date())
                    .build();
            var classroom_result =  classroomRepository.save(classroom);

            List<ClassroomStudents> classroomStudents = new ArrayList<>();
            for (User student : students) {
                var classroomStudent = ClassroomStudents.builder()
                        .classroom(classroom_result)
                        .student(student)
                        .build();
                classroomStudents.add(classroomStudentsRepository.save(classroomStudent));
            }

            return new Tuple<>(classroom_result, classroomStudents);

        } catch (Exception e) {
            log.warn("Failed to create classroom for teacher: {}, reason: {}", teacher, e.getMessage());
            return null;
        }
    }

    public void deleteClassroom(Long classroom_id) {
        try {
            var classroom = classroomRepository.findById(classroom_id).orElse(null);
            classroomStudentsRepository.deleteByClassroom(classroom);
            classroomRepository.deleteById(classroom_id);
        } catch (Exception e) {
            log.warn("Failed to delete classroom by classroom_id: {}, reason: {}", classroom_id, e.getMessage());
        }
    }

    public Set<Classroom> queryClassroomByTeacher(User teacher) {
        try {
            return new HashSet<>(classroomRepository.findAllByTeacher(teacher));
        } catch (Exception e) {
            log.warn("Failed to query classroom by teacher: {}, reason: {}", teacher, e.getMessage());
            return null;
        }
    }

    public Set<Classroom> queryClassroomByStudent(User student) {
        try {
            var result = classroomStudentsRepository.findAllByStudent(student);
            Set<Classroom> classrooms = new HashSet<>();
            for (ClassroomStudents classroomStudents : result) {
                classrooms.add(classroomStudents.getClassroom());
            }
            return classrooms;
        } catch (Exception e) {
            log.warn("Failed to query classroom by student: {}, reason: {}", student, e.getMessage());
            return null;
        }
    }

    // 帮我生成学生加入、退出班级的代码
    public void joinClassroom(User student, Classroom classroom) {
        try {
            var classroomStudent = ClassroomStudents.builder()
                    .classroom(classroom)
                    .student(student)
                    .build();
            classroomStudentsRepository.save(classroomStudent);
        } catch (Exception e) {
            log.warn("Failed to join classroom for student: {}, reason: {}", student, e.getMessage());
        }
    }

    public void leaveClassroom(User student, Classroom classroom) {
        try {
            classroomStudentsRepository.findAllByClassroom(classroom).stream()
                    .filter(cs -> cs.getStudent().equals(student))
                    .findFirst().ifPresent(classroomStudentsRepository::delete);
        } catch (Exception e) {
            log.warn("Failed to leave classroom for student: {}, reason: {}", student, e.getMessage());
        }
    }


}
