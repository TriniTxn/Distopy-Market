package com.distopy.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommomUtils {

    @Autowired
    private JavaMailSender mailSender;

    public Boolean sendMail(String url, String recipientEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("giovannisounds.dev@gmail.com", "Distopy Market - Password Reset");
        helper.setTo(recipientEmail);

        String content = "<p>Hello,</p>" + "<p>You have requested a password reset. Please click on the link below to reset your password:</p>"
                + "<p><a href=\"" + url + "\">Reset Password</a></p>";

        helper.setText(content, true);
        helper.setSubject("Distopy Market - Password Reset");
        mailSender.send(message);

        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(), "");
    }
}
