package com.tech_hub.techhub.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sentOtp(String email,String otp) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
        String emailContent = """
        <div>
            <p>Hello,Welcome to techHUB.com</p>
            <p>Your OTP for verification is: %s</p>
            <p>Follow on instagram:</p>
            <p><a href="https://instagram.com/srikand95?igshid=OGQ5ZDc2ODk2ZA==/verify-account?email=%s&otp=%s" target="blank">Click here to verify</a></p>
            <p>Thank you!</p>
        </div>
        """.formatted(otp, email, otp);
           mimeMessageHelper.setText(emailContent,true);

        System.out.println(mimeMessage);
        javaMailSender.send(mimeMessage);

    }
}
