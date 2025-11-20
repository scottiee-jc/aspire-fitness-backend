package com.SJCFIT.trial.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {

    void addWorkout(String userId, String workoutName);
    void removeWorkout(String userId, String workoutName);
    void editUserWorkout(String query);
}
