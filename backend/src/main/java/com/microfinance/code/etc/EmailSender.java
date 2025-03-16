package com.microfinance.code.etc;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailSender {

    // Static method to send email with a fixed sender, fixed password, but dynamic recipient
    public static boolean sendEmail(String to, String subject, String body) {
        String from = "richcoin973@gmail.com";    // Fixed sender's email
        String password = "bjgg xybl bgze wrbx";  // Fixed app password

        // SMTP configuration
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");  // TLS port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create session
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            // Compose the message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));  // Recipient is dynamic
            message.setSubject(subject);
            message.setText(body);

            // Send the message
            Transport.send(message);
            System.out.println("Email sent successfully.");
            return true;

        } catch (AuthenticationFailedException e) {
            System.out.println("Authentication failed. Check the credentials or app password.");
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            System.out.println("Error occurred while sending the email.");
            e.printStackTrace();
            return false;
        }
    }

    // Main method to test the static method
    public static void main(String[] args) {
        String subject = "Hello from Java!";
        String body = "This is a test email sent from a Java program.";
        String to = "b49732962@gmail.com";  // Recipient's email (dynamic)

        // Calling the static sendEmail method
        boolean result = sendEmail(to, subject, body);
        if (result) {
            System.out.println("Email was sent successfully.");
        } else {
            System.out.println("Failed to send email.");
        }
    }
}
