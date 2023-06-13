package com.nikolagrujic.tradingsimulator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nikolagrujic.tradingsimulator.exception.*;
import com.nikolagrujic.tradingsimulator.model.EmailVerificationToken;
import com.nikolagrujic.tradingsimulator.model.UserDto;
import com.nikolagrujic.tradingsimulator.repository.UserRepository;
import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.response.JwtResponse;
import com.nikolagrujic.tradingsimulator.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PortfolioService portfolioService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            EmailVerificationService emailVerificationService,
            PortfolioService portfolioService,
            ObjectMapper objectMapper,
            JwtUtil jwtUtil
        ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.portfolioService = portfolioService;
        this.portfolioService.setUserService(this); // Getting rid of circular dependency
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
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

    public JwtResponse updateUser(UserDto userDto) throws RuntimeException {
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
                user.setPassword(passwordEncoder.encode(userDto.getNewPassword()));
            }
            userRepository.save(user);
            return new JwtResponse(jwtUtil.generateJwt(email));
        }
        else throw new UserNotRegisteredException("Couldn't retrieve the user.");
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
