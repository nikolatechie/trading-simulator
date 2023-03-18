package com.nikolagrujic.tradingsimulator.service;

import com.nikolagrujic.tradingsimulator.exception.UserAlreadyExistsException;
import com.nikolagrujic.tradingsimulator.repository.UserRepository;
import lombok.AllArgsConstructor;
import com.nikolagrujic.tradingsimulator.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerNewUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists!");
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
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
}
