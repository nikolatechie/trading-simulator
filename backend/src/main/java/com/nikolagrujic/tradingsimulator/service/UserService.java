package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.exception.*;
import com.nikolagrujic.tradingsimulator.model.EmailVerificationToken;
import com.nikolagrujic.tradingsimulator.repository.UserRepository;
import com.nikolagrujic.tradingsimulator.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public void registerNewUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmailVerified(false);
        userRepository.save(user);
        LOGGER.info("User {} registered successfully", user.getEmail());
        emailVerificationService.createAndSendVerificationToken(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
        userRepository.save(user);
        LOGGER.info("Successfully verified user with token: {}", token);
    }

    @Async
    @Scheduled(cron = "0 0 0 */10 * *")
    public void cleanupUsersAndTokens() {
        LOGGER.info("Removing unused tokens and unverified users");
        List<EmailVerificationToken> expiredTokens = emailVerificationService.getAllTokensToBeRemoved();

        for (EmailVerificationToken token : expiredTokens) {
            userRepository.deleteById(token.getUser().getId()); // Token gets removed automatically
        }
    }
}
