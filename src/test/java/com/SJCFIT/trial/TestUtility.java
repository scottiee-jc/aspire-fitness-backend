package com.SJCFIT.trial;

import com.SJCFIT.trial.model.Intensity;
import com.SJCFIT.trial.model.Workout;
import com.SJCFIT.trial.model.exercise.*;
import java.util.Stack;

import static com.SJCFIT.trial.constants.ExerciseConstants.PUSH_UP_DESCRIPTION;
import static com.SJCFIT.trial.constants.ExerciseConstants.SQUAT_DESCRIPTION;

public class TestUtility {

    public Workout createMockWorkout(){
        Stack<Exercise> exerciseStack = new Stack<>();
        Exercise bodyweightExercise =
                new BodyweightExercise(1L, "Push Ups", PUSH_UP_DESCRIPTION, Intensity.MODERATE.getValue(), 2, 15);
        Exercise weightLiftingExercise =
                new WeightliftingExercise(1L, "Squats", SQUAT_DESCRIPTION, Intensity.HIGH.getValue(), 2, 10, new Weight(80, "KG"), new Equipment("Barbell", null));
        exerciseStack.push(bodyweightExercise);
        exerciseStack.push(weightLiftingExercise);
        return new Workout(1L, "StarterWorkout", "A description of a good starter workout", exerciseStack);
    }

}
