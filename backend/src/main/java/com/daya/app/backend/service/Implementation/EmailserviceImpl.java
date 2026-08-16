package com.daya.app.backend.service.Implementation;

import com.daya.app.backend.entity.OtpPurpose;
import com.daya.app.backend.exception.ApiException;
import com.daya.app.backend.exception.ErrorCode;
import com.daya.app.backend.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailserviceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    @Override
    public void sendOtp(
            String email,
            String displayName,
            String otp,
            OtpPurpose purpose
    ) {

        String subject = getSubject(purpose);
        String body = buildEmailBody(
                displayName,
                otp,
                purpose
        );

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            /*
             * Explicitly set the authenticated Yahoo
             * account as the sender.
             */
            helper.setFrom(senderEmail);

            /*
             * Recipient supplied during registration/login/etc.
             */
            helper.setTo(email);

            helper.setSubject(subject);

            helper.setText(
                    body,
                    true
            );

            mailSender.send(message);
            long start = System.currentTimeMillis();

mailSender.send(message);

long end = System.currentTimeMillis();

System.out.println(
    "OTP email SMTP send time: " +
    (end - start) +
    " ms"
);

        } catch (MessagingException | MailException exception) {

            throw new ApiException(
                    ErrorCode.EMAIL_SEND_FAILED,
                    "Unable to send verification email."
            );
        }
    }

    private String getSubject(OtpPurpose purpose) {

        return switch (purpose) {

            case LOGIN ->
                    "Your Login Verification Code";

            case REGISTER ->
                    "Verify Your Email Address";

            case RESET_PASSWORD ->
                    "Your Password Reset Code";

            case CHANGE_PRIMARY_EMAIL ->
                    "Verify Your New Primary Email";

            case CHANGE_ALTERNATE_EMAIL ->
                    "Verify Your Alternate Email";

            default ->
                    throw new ApiException(
                            ErrorCode.OTP_PURPOSE_NOT_SUPPORTED,
                            "Email OTP is not supported for this OTP purpose."
                    );
        };
    }

    private String buildEmailBody(
            String displayName,
            String otp,
            OtpPurpose purpose
    ) {

        String action = switch (purpose) {

            case LOGIN ->
                    "complete your login";

            case REGISTER ->
                    "verify your email address";

            case RESET_PASSWORD ->
                    "reset your password";

            case CHANGE_PRIMARY_EMAIL ->
                    "verify your new primary email address";

            case CHANGE_ALTERNATE_EMAIL ->
                    "verify your alternate email address";

            default ->
                    throw new ApiException(
                            ErrorCode.OTP_PURPOSE_NOT_SUPPORTED,
                            "Email OTP is not supported for this OTP purpose."
                    );
        };

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Verification Code</title>
                </head>

                <body style="font-family: Arial, sans-serif;">

                    <h2>Hello %s,</h2>

                    <p>
                        Use the following verification code to %s:
                    </p>

                    <h1 style="letter-spacing: 6px;">
                        %s
                    </h1>

                    <p>
                        This code will expire in 5 minutes.
                    </p>

                    <p>
                        If you did not request this code,
                        please ignore this email.
                    </p>

                    <p>
                        Regards,<br>
                        Apartment Manager
                    </p>

                </body>
                </html>
                """.formatted(
                escapeHtml(displayName),
                action,
                escapeHtml(otp)
        );
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}