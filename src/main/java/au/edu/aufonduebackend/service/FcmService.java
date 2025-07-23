package au.edu.aufonduebackend.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Service
public class FcmService {

    private static final Logger logger = LoggerFactory.getLogger(FcmService.class);

    public boolean sendIssueUpdateNotification(String fcmToken, Long issueId, String status, String comment) {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("issueId", issueId.toString());
            data.put("updateType", "status_update");
            data.put("status", status);
            if (comment != null) {
                data.put("comment", comment);
            }

            String title = getNotificationTitle(status);
            String body = getNotificationBody(status, comment);

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("ic_notification")
                                    .setChannelId("au_fondue_notifications")
                                    .setPriority(AndroidNotification.Priority.HIGH)
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: {}", response);
            return true;

        } catch (Exception e) {
            logger.error("Error sending FCM notification to token: {}", fcmToken, e);
            return false;
        }
    }

    public boolean sendTestNotification(String fcmToken, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder()
                            .setNotification(AndroidNotification.builder()
                                    .setIcon("ic_notification")
                                    .setChannelId("au_fondue_notifications")
                                    .setPriority(AndroidNotification.Priority.HIGH)
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent test message: {}", response);
            return true;

        } catch (Exception e) {
            logger.error("Error sending test FCM notification", e);
            return false;
        }
    }

    private String getNotificationTitle(String status) {
        switch (status.toUpperCase()) {
            case "IN_PROGRESS":
                return "Issue in Progress";
            case "COMPLETED":
                return "Issue Completed";
            case "REJECTED":
                return "Issue Reviewed";
            default:
                return "Issue Update";
        }
    }

    private String getNotificationBody(String status, String comment) {
        String baseMessage = switch (status.toUpperCase()) {
            case "IN_PROGRESS" -> "Your reported issue is now being worked on";
            case "COMPLETED" -> "Your reported issue has been resolved";
            case "REJECTED" -> "Your reported issue has been reviewed";
            default -> "There's an update on your reported issue";
        };

        if (comment != null && !comment.trim().isEmpty()) {
            return baseMessage + ": " + comment;
        }

        return baseMessage;
    }
}