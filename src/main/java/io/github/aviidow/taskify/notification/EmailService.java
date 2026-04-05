package io.github.aviidow.taskify.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Async("emailExecutor")
    public void sendTaskAssignmentEmail(String toEmail, String taskTitle, String assigneeName) {
        try {
            log.info("Sending email to {} about task: {}", toEmail, taskTitle);

            Thread.sleep(2000);

            log.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email {}: {}", toEmail, e.getMessage());
        }
    }

    @Async("emailExecutor")
    public void sendNewCommentEmail(String toEmail, String taskTitle, String authorName) {
        try {
            log.info("Sending comment notification to {} for task: {}", toEmail, taskTitle);
            Thread.sleep(1000);
            log.info("Comment notification sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send comment notification: {}", e.getMessage());
        }
    }
}
