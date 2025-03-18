package com.microfinance.code.etc;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SmsSender {

    // Twilio Credentials
    private static final String ACCOUNT_SID = "AC355c2709baa47d2978930c19217016ad";
    private static final String AUTH_TOKEN = "9e57321fa19041add7387a79a6af1d28";

    // Twilio Sender Phone Number
    private static final String TWILIO_PHONE_NUMBER = "+15104690481";

    // Initialize Twilio once
    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    /**
     * ✅ Reusable method to send SMS
     * @param toPhoneNumber Recipient Phone Number (e.g., +95979xxxxxxx)
     * @param messageBody   The message you want to send
     */
    public static void sendSms(String toPhoneNumber, String messageBody) {
        try {
            Message message = Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(TWILIO_PHONE_NUMBER),
                    messageBody
            ).create();

            System.out.println("✅ SMS sent successfully! SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("❌ Failed to send SMS: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        // 📱 Recipient's phone number (make sure to use E.164 format)
        String recipientPhoneNumber = "+959793616719"; // Replace with a real phone number
        String messageBody = "Hello from Microfinance System! 📨";

        // 📤 Send SMS
        SmsSender.sendSms(recipientPhoneNumber, messageBody);
    }
}
