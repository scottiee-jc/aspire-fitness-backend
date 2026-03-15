package com.SJCFIT.trial.implementations;

import com.SJCFIT.trial.model.Workout;
import com.SJCFIT.trial.repository.WorkoutRepository;
import com.SJCFIT.trial.service.WorkoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutServiceImpl implements WorkoutService {

    @Autowired
    WorkoutRepository workoutRepository;

    public Workout findWorkoutByName(String workoutName){
        List<Workout> workouts = workoutRepository.findAll();
        return workouts.stream().filter(workout -> workout.getName().matches(workoutName)).findFirst().get();
    }

    @Override
    public void createWorkout(String query) {

    }
}
