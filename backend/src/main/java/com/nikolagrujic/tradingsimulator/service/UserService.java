package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.exception.ExpiredTokenException;
import com.nikolagrujic.tradingsimulator.exception.InvalidTokenException;
import com.nikolagrujic.tradingsimulator.exception.UserAlreadyExistsException;
import com.nikolagrujic.tradingsimulator.model.EmailVerificationToken;
import com.nikolagrujic.tradingsimulator.repository.UserRepository;
import lombok.AllArgsConstructor;
import com.nikolagrujic.tradingsimulator.model.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Transactional
    public void registerNewUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists!");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailVerified(false);
            userRepository.save(user);
            emailVerificationService.createAndSendVerificationToken(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + email + " doesn't exist!");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    @Transactional
    public void verifyUser(String token) throws InvalidTokenException, ExpiredTokenException {
        EmailVerificationToken savedToken = emailVerificationService.getByToken(token);
        User user = savedToken.getUser();
        if (emailVerificationService.isExpiredToken(savedToken.getExpiryDateTime())) {
            emailVerificationService.deleteById(savedToken.getId()); // Removing old token
            emailVerificationService.createAndSendVerificationToken(user); // Create and send a new token
            throw new ExpiredTokenException("The token expired. The new one will arrive shortly.");
        }
        user.setEmailVerified(true);
        emailVerificationService.deleteById(savedToken.getId());
        userRepository.save(user);
    }

    @Async
    @Scheduled(cron = "0 0 0 */10 * *")
    public void cleanupUsersAndTokens() {
        System.out.println("[" + LocalDateTime.now() + "] Removing unused tokens and unverified users");
        List<EmailVerificationToken> expiredTokens = emailVerificationService.getAllTokensToBeRemoved();

        for (EmailVerificationToken token: expiredTokens) {
            userRepository.deleteById(token.getUser().getId());
            // Token gets removed automatically
        }
    }
}
