package com.SJCFIT.trial.implementations;

import com.SJCFIT.trial.model.Workout;
import com.SJCFIT.trial.model.user.User;
import com.SJCFIT.trial.repository.UserRepository;
import com.SJCFIT.trial.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public void addWorkout(String userId, String workoutName){
        User user = userRepository.findUser(userId);
        List<Workout> userWorkouts = user.getUserSavedWorkouts();
        if (!doesExist(userWorkouts, workoutName)){
            Workout workout = userWorkouts.stream().filter(workout1 -> workout1.getName().matches(workoutName)).findFirst().get();
            userWorkouts.add(workout);
        } else {
            throw new RuntimeException("Cannot add " + workoutName + " as it already exists");
        }
    }

    @Override
    public void removeWorkout(String userId, String workoutName){
        User user = userRepository.findUser(userId);
        List<Workout> userWorkouts = user.getUserSavedWorkouts();
        if (doesExist(userWorkouts, workoutName)){
            userWorkouts.stream()
                    .filter(workout -> workout.getName().matches(workoutName))
                    .findFirst()
                    .get();
        } else {
            throw new RuntimeException("Cannot remove " + workoutName + " as it doesn't exist");
        }
    }

    @Override
    public void editUserWorkout(String query) {
        // implement logic (potentially through a switch function?) to retrieve database logic and return
    }

    private boolean doesExist(List<Workout> workouts, String workoutName){
        return workouts.stream().anyMatch(workout -> workout.getName().matches(workoutName));
    }
}
