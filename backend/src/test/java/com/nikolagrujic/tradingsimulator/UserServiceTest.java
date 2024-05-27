package com.nikolagrujic.tradingsimulator;

import com.nikolagrujic.tradingsimulator.model.User;
import com.nikolagrujic.tradingsimulator.repository.UserRepository;
import com.nikolagrujic.tradingsimulator.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testGetUserByEmail() {
        final String EMAIL = "test@test.com";
        final String FIRST_NAME = "First";
        User mockUser = new User();
        mockUser.setEmail(EMAIL);
        mockUser.setPassword("password");
        mockUser.setFirstName(FIRST_NAME);
        mockUser.setLastName("Last");
        userRepository.save(mockUser);

        User user = userService.findByEmail(EMAIL);
        assertNotNull(user);
        assertEquals(user.getFirstName(), FIRST_NAME);
    }
}
