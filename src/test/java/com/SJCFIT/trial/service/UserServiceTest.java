package com.SJCFIT.trial.service;

import com.SJCFIT.trial.TestUtility;
import com.SJCFIT.trial.model.Workout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    UserService userService;
    TestUtility testUtility;
    @Test()
    @DisplayName("User should be able to add workout to his workouts")
    void testOne(){
        Workout workout = testUtility.createMockWorkout();
//        userService.addWorkout(workout);
    }

    @Test()
    @DisplayName("User should be able to remove workout from his workouts")
    void testTwo(){

    }
}
