package com.distopy.util;

import com.distopy.model.ProductOrder;
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

    String msg = "<p>[[name]]</p><br><p>Thank you for ordering.</p>"
            + "<p><b>[[orderStatus]]</b></p>"
            + "<p><b>Product Details: </b></p>"
            + "<p>Name: [[productName]]</p>"
            + "<p>Category: [[category]]</p>"
            + "<p>Quantity: [[quantity]]</p>"
            + "<p>Price: [[price]]</p>"
            + "<p>Payment Type: [[paymentType]]</p>";


    public Boolean sendMailForProductOrder(ProductOrder order, String status) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("giovannisounds.dev@gmail.com", "Distopy Market - Password Reset");
        helper.setTo(order.getOrderAddress().getEmail());

        msg = msg.replace("[[name]]", order.getOrderAddress().getFirstName() + " " + order.getOrderAddress().getLastName());
        msg = msg.replace("[[orderStatus]]", status);
        msg = msg.replace("[[productName]]", order.getProduct().getTitle());
        msg = msg.replace("[[category]]", order.getProduct().getCategory());
        msg = msg.replace("[[quantity]]", order.getQuantity().toString());
        msg = msg.replace("[[price]]", order.getPrice().toString());
        msg = msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }

}
