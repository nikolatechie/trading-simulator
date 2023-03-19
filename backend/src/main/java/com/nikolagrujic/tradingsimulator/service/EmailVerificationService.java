package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.exception.InvalidTokenException;
import com.nikolagrujic.tradingsimulator.model.EmailVerificationToken;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.repository.EmailVerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@AllArgsConstructor
@Service
public class EmailVerificationService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;

    public boolean isExpiredToken(LocalDateTime expiryDateTime) {
        return expiryDateTime.isBefore(LocalDateTime.now());
    }

    public void deleteById(Long id) {
        emailVerificationTokenRepository.deleteById(id);
    }

    public EmailVerificationToken getByToken(String token) throws InvalidTokenException {
        EmailVerificationToken savedToken = emailVerificationTokenRepository.findByToken(token);
        if (savedToken == null) {
            throw new InvalidTokenException("The token is invalid!");
        }
        return savedToken;
    }

    public void sendVerificationToken(User user) {
        EmailVerificationToken token = createToken(user);
        emailVerificationTokenRepository.save(token);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Verify Email to access Trading Simulator");
        mailMessage.setText(createEmailText(user, token.getToken()));
        eventPublisher.publishEvent(mailMessage);
    }

    private EmailVerificationToken createToken(User user) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDateTime(LocalDateTime.now().plusMinutes(30));
        return token;
    }

    private String createEmailText(User user, String token) {
        return "Dear " + user.getFirstName() + ",\n" +
                "Please click the following link to verify your email at Trading Simulator: " +
                "http://localhost:8080/api/user/verify?token=" + token + " (it expires in 30 minutes).\n\n" +
                "Kind regards,\nTrading Simulator";
    }

    @EventListener
    public void sendEmailForVerification(SimpleMailMessage mailMessage) {
        System.out.println("[" + LocalDateTime.now() + "] Sending a verification email to: " + Arrays.toString(mailMessage.getTo()));
        mailSender.send(mailMessage);
    }
}
