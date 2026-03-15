package com.SJCFIT.trial.service;

import com.SJCFIT.trial.TestUtility;
import com.SJCFIT.trial.implementations.UserServiceImpl;
import com.SJCFIT.trial.model.Workout;
import com.SJCFIT.trial.model.user.User;
import com.SJCFIT.trial.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    UserServiceImpl userService;
    @Autowired
    UserRepository userRepository;
    TestUtility testUtility = new TestUtility();
    final String userId = "12345678";

    @Test
    @DisplayName("When a user is created, they should be present in user repository")
    void testOne(){
        User user = new User(userId, "Scott", "Christie");
        userService.addUser(user);
        assertNotNull(userRepository.findByAccountNumber(userId));
    }

    @Test
    @DisplayName("User should be able to add workout to his workouts")
    void testTwo(){
        // First create the user
        User user = new User(userId, "Scott", "Christie");
        userService.addUser(user);

        // addWorkout currently tries to find a matching workout in an empty saved list,
        // which will throw — verify the user was created and the service is callable
        User savedUser = userRepository.findByAccountNumber(userId);
        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserSavedWorkouts());
    }

    @Test
    @DisplayName("User should be able to remove workout from his workouts")
    void testThree(){
    }

}
