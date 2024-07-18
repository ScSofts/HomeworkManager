package edu.njust.homework_manager.backend.services;

import edu.njust.homework_manager.backend.models.Homework;
import edu.njust.homework_manager.backend.models.Classroom;
import edu.njust.homework_manager.backend.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Repository
interface HomeworkRepository extends JpaRepository<Homework, Long> {
    List<Homework> findAllByClassroom(Classroom classroom);
    List<Homework> findAllByClassroomAndDeadlineAfter(Classroom classroom, Date date);
}

@Service
public class HomeworkService {

    private static final Logger log = LoggerFactory.getLogger(HomeworkService.class);
    private final HomeworkRepository homeworkRepository;

    @Autowired
    private HomeworkService(HomeworkRepository homeworkRepository) {
        this.homeworkRepository = homeworkRepository;
    }

    @Nullable
    public Homework createHomework(Classroom classroom, String title, String description, Date deadline) {
        try {
            var homework = Homework.builder()
                    .classroom(classroom)
                    .title(title)
                    .description(description)
                    .deadline(deadline)
                    .created_at(new Date())
                    .build();
            return homeworkRepository.save(homework);
        } catch (Exception e) {
            log.warn("Failed to create homework for classroom: {}, title: {}, reason: {}", classroom, title, e.getMessage());
            return null;
        }
    }

    public void deleteHomework(Long homework_id) {
        try {
            homeworkRepository.deleteById(homework_id);
        } catch (Exception e) {
            log.warn("Failed to delete homework by homework_id: {}, reason: {}", homework_id, e.getMessage());
        }
    }

    public Homework queryHomework(Long homework_id) {
        try {
            return homeworkRepository.findById(homework_id ).orElse(null);
        } catch (Exception e) {
            log.warn("Failed to query homework by homework_id: {}, reason: {}", homework_id, e.getMessage());
            return null;
        }
    }

    public List<Homework> queryHomeworkByClassroom(Classroom classroom) {
        try {
            return homeworkRepository.findAllByClassroom(classroom);
        } catch (Exception e) {
            log.warn("Failed to query homework by classroom: {}, reason: {}", classroom, e.getMessage());
            return null;
        }
    }

    public List<Homework> queryActiveHomeworkByClassroom(Classroom classroom) {
        try {
            return homeworkRepository.findAllByClassroomAndDeadlineAfter(classroom, new Date());
        } catch (Exception e) {
            log.warn("Failed to query active homework by classroom: {}, reason: {}", classroom, e.getMessage());
            return null;
        }
    }

    @Nullable
    public Homework getHomework(Long homework_id) {
        try {
            return homeworkRepository.findById(homework_id).orElse(null);
        } catch (Exception e) {
            log.warn("Failed to get homework by id: {}, reason: {}", homework_id, e.getMessage());
            return null;
        }
    }

    public boolean updateHomework(Long homework_id, String title, String description, Date deadline) {
        try {
            var homeworkOptional = homeworkRepository.findById(homework_id);
            if (homeworkOptional.isPresent()) {
                var homework = homeworkOptional.get();
                homework.setTitle(title);
                homework.setDescription(description);
                homework.setDeadline(deadline);
                homeworkRepository.save(homework);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to update homework: {}, reason: {}", homework_id, e.getMessage());
            return false;
        }
    }

    public boolean isHomeworkOverdue(Long homework_id) {
        try {
            var homeworkOptional = homeworkRepository.findById(homework_id);
            if (homeworkOptional.isPresent()) {
                var homework = homeworkOptional.get();
                return new Date().after(homework.getDeadline());
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to check if homework is overdue: {}, reason: {}", homework_id, e.getMessage());
            return false;
        }
    }
}