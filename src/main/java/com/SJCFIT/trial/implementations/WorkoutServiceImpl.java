package com.SJCFIT.trial.implementations;

import com.SJCFIT.trial.model.Workout;
import com.SJCFIT.trial.repository.WorkoutRepository;
import com.SJCFIT.trial.service.WorkoutService;

import java.util.List;

public class WorkoutServiceImpl implements WorkoutService {
    WorkoutRepository workoutRepository;

    public Workout findWorkoutByName(String workoutName){
        List<Workout> workouts = workoutRepository.findAll();
        return workouts.stream().filter(workout -> workout.getName().matches(workoutName)).findFirst().get();
    }

    @Override
    public void createWorkout(String query) {

    }
}
