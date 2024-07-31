package com.tech_hub.techhub.service.token;

import com.tech_hub.techhub.entity.PasswordResetToken;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.repository.TokenRepository;
import com.tech_hub.techhub.repository.UserRepository;
import com.tech_hub.techhub.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService{

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public String sentEmail(UserEntity user) {
        try {
            String resetLink = generateResetToken(user);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("sreekanthv1995@gmail.com");
            message.setTo(user.getEmail());

            message.setSubject("Welcome to techHUB");
            message.setText("Hello \n\n" + "Please click on this link to Reset your Password :" + resetLink + ". \n\n"
                    + "Regards \n" + "techHUB");
            javaMailSender.send(message);
            return "success";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "error";
    }

    @Override
    public boolean hasExpired(LocalDateTime expiryDateTime) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        return expiryDateTime.isAfter(currentDateTime);
    }

    private String generateResetToken(UserEntity user) {
        PasswordResetToken existingToken = tokenRepository.findByUser(user);
        if (existingToken != null){
            existingToken.setToken(UUID.randomUUID().toString());
            existingToken.setExpiryDateTime(LocalDateTime.now().plusMinutes(1));
            tokenRepository.save(existingToken);
            return getResultLink(existingToken);
        }else {
            PasswordResetToken newToken = new PasswordResetToken();
            newToken.setUser(user);
            newToken.setToken(UUID.randomUUID().toString());
            newToken.setExpiryDateTime(LocalDateTime.now().plusMinutes(1));
            newToken.setUser(user);
            tokenRepository.save(newToken);
            return getResultLink(newToken);
        }
    }

    private String getResultLink(PasswordResetToken token) {

        String endPointUrl = "https://www.techhubstore.online/resetPassword";
        System.out.println(endPointUrl + "/" + token.getToken());
        return endPointUrl + "/" + token.getToken();
    }
}
