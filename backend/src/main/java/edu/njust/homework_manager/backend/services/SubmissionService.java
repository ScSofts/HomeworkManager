package edu.njust.homework_manager.backend.services;

import edu.njust.homework_manager.backend.models.Submission;
import edu.njust.homework_manager.backend.models.User;
import edu.njust.homework_manager.backend.models.Homework;
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
interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findAllByHomework(Homework homework);
    List<Submission> findAllByStudent(User student);
    Submission findByHomeworkAndStudent(Homework homework, User student);
}

@Service
public class SubmissionService {

    private static final Logger log = LoggerFactory.getLogger(SubmissionService.class);
    private final SubmissionRepository submissionRepository;

    @Autowired
    private SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Nullable
    public Submission createSubmission(Homework homework, User student, String content) {
        try {
            var submission = Submission.builder()
                    .homework(homework)
                    .student(student)
                    .content(content)
                    .status(Submission.Status.PENDING)
                    .submit_at(new Date())
                    .build();
            return submissionRepository.save(submission);
        } catch (Exception e) {
            log.warn("Failed to create submission for homework: {}, student: {}, reason: {}", homework, student, e.getMessage());
            return null;
        }
    }

    public void deleteSubmission(Long submission_id) {
        try {
            submissionRepository.deleteById(submission_id);
        } catch (Exception e) {
            log.warn("Failed to delete submission by submission_id: {}, reason: {}", submission_id, e.getMessage());
        }
    }

    public List<Submission> querySubmissionsByHomework(Homework homework) {
        try {
            return submissionRepository.findAllByHomework(homework);
        } catch (Exception e) {
            log.warn("Failed to query submissions by homework: {}, reason: {}", homework, e.getMessage());
            return null;
        }
    }

    public List<Submission> querySubmissionsByStudent(User student) {
        try {
            return submissionRepository.findAllByStudent(student);
        } catch (Exception e) {
            log.warn("Failed to query submissions by student: {}, reason: {}", student, e.getMessage());
            return null;
        }
    }

    @Nullable
    public Submission getSubmission(Homework homework, User student) {
        try {
            return submissionRepository.findByHomeworkAndStudent(homework, student);
        } catch (Exception e) {
            log.warn("Failed to get submission for homework: {}, student: {}, reason: {}", homework, student, e.getMessage());
            return null;
        }
    }

    @Nullable
    public Submission getSubmissionById(Long submission_id) {
        try {
            var submissionOptional = submissionRepository.findById(submission_id);
            return submissionOptional.orElse(null);
        } catch (Exception e) {
            log.warn("Failed to get submission by submission_id: {}, reason: {}", submission_id, e.getMessage());
            return null;
        }
    }

    public boolean updateSubmission(Long submission_id, String content) {
        try {
            var submissionOptional = submissionRepository.findById(submission_id);
            if (submissionOptional.isPresent()) {
                var submission = submissionOptional.get();
                submission.setContent(content);
                submission.setSubmit_at(new Date());
                submissionRepository.save(submission);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to update submission: {}, reason: {}", submission_id, e.getMessage());
            return false;
        }
    }
}