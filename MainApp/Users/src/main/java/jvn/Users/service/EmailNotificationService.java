package jvn.Users.service;

public interface EmailNotificationService {
    void sendEmail(String to, String subject, String text);
}

