package edu.njust.homework_manager.backend.services;

import edu.njust.homework_manager.backend.models.Grading;
import edu.njust.homework_manager.backend.models.Submission;
import edu.njust.homework_manager.backend.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
interface GradingRepository extends JpaRepository<Grading, Long> {
    List<Grading> findAllByTeacher(User teacher);
    Grading findBySubmission(Submission submission);
}

@Service
public class GradingService {

    private static final Logger log = LoggerFactory.getLogger(GradingService.class);
    private final GradingRepository gradingRepository;
    private final SubmissionRepository submissionRepository;

    @Autowired
    protected GradingService(GradingRepository gradingRepository, SubmissionRepository submissionRepository) {
        this.gradingRepository = gradingRepository;
        this.submissionRepository = submissionRepository;
    }


    @Nullable
    @Transactional
    public Grading createGrading(Submission submission, User grader, int score, String comment) {
        try {
            var grading = Grading.builder()
                    .submission(submission)
                    .teacher(grader)
                    .score(score)
                    .comment(comment)
                    .graded_at(new Date())
                    .build();

            // Update submission status
            submission.setStatus(Submission.Status.ACCEPTED);
            submissionRepository.save(submission);

            return gradingRepository.save(grading);
        } catch (Exception e) {
            log.warn("Failed to create grading for submission: {}, grader: {}, reason: {}", submission, grader, e.getMessage());
            return null;
        }
    }

    @Transactional
    public void deleteGrading(Long grading_id) {
        try {
            var gradingOptional = gradingRepository.findById(grading_id);
            if (gradingOptional.isPresent()) {
                var grading = gradingOptional.get();
                var submission = grading.getSubmission();
                submission.setStatus(Submission.Status.PENDING);
                submissionRepository.save(submission);
                gradingRepository.deleteById(grading_id);
            }
        } catch (Exception e) {
            log.warn("Failed to delete grading by grading_id: {}, reason: {}", grading_id, e.getMessage());
        }
    }

    public List<Grading> queryGradingsByTeacher(User teacher) {
        try {
            return gradingRepository.findAllByTeacher(teacher);
        } catch (Exception e) {
            log.warn("Failed to query gradings by grader: {}, reason: {}", teacher, e.getMessage());
            return null;
        }
    }

    @Nullable
    public Grading getGradingBySubmission(Submission submission) {
        try {
            return gradingRepository.findBySubmission(submission);
        } catch (Exception e) {
            log.warn("Failed to get grading for submission: {}, reason: {}", submission, e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean updateGrading(Long grading_id, int score, String comment) {
        try {
            var gradingOptional = gradingRepository.findById(grading_id);
            if (gradingOptional.isPresent()) {
                var grading = gradingOptional.get();
                grading.setScore(score);
                grading.setComment(comment);
                grading.setGraded_at(new Date());

                // Ensure submission status is GRADED
                var submission = grading.getSubmission();
                if (submission.getStatus() != Submission.Status.ACCEPTED) {
                    submission.setStatus(Submission.Status.ACCEPTED);
                    submissionRepository.save(submission);
                }

                gradingRepository.save(grading);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.warn("Failed to update grading: {}, reason: {}", grading_id, e.getMessage());
            return false;
        }
    }
}