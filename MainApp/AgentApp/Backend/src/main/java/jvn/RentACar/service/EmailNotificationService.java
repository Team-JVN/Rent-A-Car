package jvn.RentACar.service;

public interface EmailNotificationService {
    void sendEmail(String to, String subject, String text);
}
