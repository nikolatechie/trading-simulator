package com.nikolagrujic.tradingsimulator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.constants.Constants;
import com.nikolagrujic.tradingsimulator.dto.*;
import com.nikolagrujic.tradingsimulator.event.ResetPasswordEvent;
import com.nikolagrujic.tradingsimulator.exception.*;
import com.nikolagrujic.tradingsimulator.model.*;
import com.nikolagrujic.tradingsimulator.repository.ResetPasswordTokenRepository;
import com.nikolagrujic.tradingsimulator.repository.UserRepository;
import com.nikolagrujic.tradingsimulator.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ResetPasswordTokenRepository tokenRepository;
    private final PortfolioService portfolioService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final JavaMailSender mailSender;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            ResetPasswordTokenRepository tokenRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService,
            PortfolioService portfolioService,
            JavaMailSender mailSender,
            ApplicationEventPublisher eventPublisher,
            ObjectMapper objectMapper,
            JwtUtil jwtUtil,
            ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.portfolioService = portfolioService;
        this.portfolioService.setUserService(this); // Getting rid of circular dependency
        this.mailSender = mailSender;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    @Transactional
    public void registerNewUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException(
                "User with email " + user.getEmail() + " already exists!",
                user.getEmail()
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        userRepository.save(user);
        LOGGER.info("User {} registered successfully", user.getEmail());
        portfolioService.createUserPortfolio(user);
        LOGGER.info("User portfolio has been created");
        emailVerificationService.createAndSendVerificationToken(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public ObjectNode getUserSettingsInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        ObjectNode userInfo = objectMapper.createObjectNode();
        if (user != null) {
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("email", user.getEmail());
        }
        return userInfo;
    }

    public JwtDto updateUser(UserDto userDto) throws RuntimeException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Updating user info: {}", email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (!passwordEncoder.matches(userDto.getCurrentPassword(), user.getPassword())) {
               throw new InvalidPasswordException("The current password you've entered is not correct.");
            }
            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            if (!userDto.getNewPassword().isEmpty()) {
                if (!userDto.getNewPassword().equals(userDto.getNewPasswordRepeat())) {
                    throw new InvalidPasswordException("The new passwords don't match.");
                }
                if (userDto.getNewPassword().length() < Constants.PASSWORD_MIN_LENGTH) {
                    throw new InvalidPasswordException("The password must contain at least 8 characters.");
                }
                user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
            }
            userRepository.save(user);
            return new JwtDto(jwtUtil.generateJwt(email));
        }
        else throw new UserNotRegisteredException("Couldn't retrieve the user.", null);
    }

    public void updatePassword(ResetPasswordRequest request) {
        if (request == null) {
            throw new IncompleteBodyException("Couldn't update the password due to missing fields in the request.");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordRepeat())) {
            throw new InvalidPasswordException("The new passwords don't match.");
        }
        ResetPasswordToken resetToken = resetPasswordTokenRepository.findByToken(request.getToken());
        if (resetToken == null) {
            throw new InvalidTokenException("The reset token doesn't exist.", request.getToken());
        }
        if (resetToken.getExpiryDateTime().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("The token has expired. Please request a new password reset link.");
        }
        // Update password
        User user = resetToken.getUser();
        LOGGER.info("Updating password via reset link: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        // Remove token
        resetPasswordTokenRepository.deleteById(resetToken.getId());
    }

    public ResponseEntity<?> sendResetPasswordLink(LoginRequest loginRequest) {
        User user = null;
        if (loginRequest != null) {
            user = userRepository.findByEmail(loginRequest.getEmail());
        }
        if (user == null) {
            return ResponseEntity.status(400).body(
                new ErrorResponse("User with entered email doesn't exist!")
            );
        }
        LOGGER.info("Creating a reset password token for {}", loginRequest.getEmail());
        ResetPasswordToken token = createToken(user);
        tokenRepository.save(token);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Reset your password | Trading Simulator");
        mailMessage.setText(createEmailText(user, token.getToken()));
        eventPublisher.publishEvent(new ResetPasswordEvent(mailMessage));
        return ResponseEntity.ok().build();
    }

    private ResetPasswordToken createToken(User user) {
        ResetPasswordToken token = new ResetPasswordToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDateTime(LocalDateTime.now().plusMinutes(Constants.EMAIL_TOKEN_EXPIRATION_MINUTES));
        return token;
    }

    private String createEmailText(User user, String token) {
        return "Dear " + user.getFirstName() + ",\n" +
                "We have received a request to reset your password. " +
                "Please click the following link to do so: " +
                "http://localhost:3000" + Constants.RESET_PASSWORD_PATH +
                "?token=" + token + " (it expires in " + Constants.EMAIL_TOKEN_EXPIRATION_MINUTES +
                " minutes).\n\nKind regards,\nTrading Simulator";
    }

    /**
     * Event listener that sends a reset password token by email. Should NOT be called manually!
     * @param event object containing data about sender, receiver, subject etc.
     */
    @Async
    @EventListener
    public void sendResetPasswordTokenOnEmail(ResetPasswordEvent event) {
        LOGGER.info(
            "Sending a reset password token on email {}",
            Objects.requireNonNull(event.getSimpleMailMessage().getTo())[0]
        );
        mailSender.send(event.getSimpleMailMessage());
    }

    @Transactional
    public void deleteAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        LOGGER.info("Deleting an account: {}", email);
        userRepository.deleteByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + email + " isn't registered!");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    @Transactional(noRollbackFor = ExpiredTokenException.class)
    public void verifyUser(String token) throws InvalidTokenException, ExpiredTokenException {
        EmailVerificationToken savedToken = emailVerificationService.getByToken(token);
        emailVerificationService.deleteById(savedToken.getId()); // Removing old token
        User user = savedToken.getUser();
        if (emailVerificationService.isExpiredToken(savedToken.getExpiryDateTime())) {
            LOGGER.warn("The token {} expired. Creating and sending a new one...", token);
            emailVerificationService.createAndSendVerificationToken(user);
            throw new ExpiredTokenException("The token expired. A new one will arrive shortly."); // No rollback here
        }
        user.setEmailVerified(true);
        user.setVerificationDate(LocalDate.now());
        userRepository.save(user);
        LOGGER.info("Successfully verified user with token: {}", token);
    }

    public LocalDate getVerificationDate() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.getVerificationDate(email);
    }

    public ObjectNode getFullName() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        ObjectNode fullName = objectMapper.createObjectNode();
        fullName.put("fullName", user.getFirstName() + " " + user.getLastName());
        return fullName;
    }

    @Async
    @Scheduled(cron = "0 0 0 */10 * *")
    public void cleanupUsersAndTokens() {
        LOGGER.info("Removing unused tokens and unverified users");

        List<EmailVerificationToken> verificationTokens = emailVerificationService.getAllTokensToBeRemoved();
        for (EmailVerificationToken token : verificationTokens) {
            userRepository.deleteById(token.getUser().getId()); // Token gets removed automatically
        }

        List<ResetPasswordToken> resetTokens = resetPasswordTokenRepository.findAll();
        for (ResetPasswordToken token : resetTokens) {
            if (token.getExpiryDateTime().isBefore(LocalDateTime.now())) {
                resetPasswordTokenRepository.delete(token);
            }
        }
    }
}
