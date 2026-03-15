package com.SJCFIT.trial.service;

import com.SJCFIT.trial.model.user.User;

public interface UserService {

    void addUser(User user);
    void addWorkout(String userId, String workoutName);
    void removeWorkout(String userId, String workoutName);
    void editUserWorkout(String query);
    User getUser(String token);
}
