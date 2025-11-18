package com.SJCFIT.trial.model.user;

import com.SJCFIT.trial.model.Workout;

import java.util.List;

public record UserWorkouts (List<Workout> workoutsList) {

    public Workout getWorkout(String name){
        return workoutsList
                .stream()
                .filter(workout -> workout.getName().matches(name))
                .findFirst()
                .get();
    }
}
