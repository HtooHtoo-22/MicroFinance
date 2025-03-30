package com.microfinance.code.etc;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    public static boolean sendEmail(String to, String subject, String body) {
        String from = "richcoin973@gmail.com";
        String password = "bjgg xybl bgze wrbx";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Add these security properties
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.checkserveridentity", "true");

        // For debugging
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully.");
            return true;

        } catch (Exception e) {
            System.out.println("Error occurred while sending the email.");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String subject = "Hello from Java!";
        String body = "This is a test email sent from a Java program.";
        String to = "b49732962@gmail.com";

        boolean result = sendEmail(to, subject, body);
        System.out.println(result ? "Email was sent successfully." : "Failed to send email.");
    }
}