package com.SJCFIT.trial.utility;

import com.SJCFIT.trial.model.Workout;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WorkoutCreatorUtility {

    public Workout parseQueryIntoExercise(Map<String, String> queries){
        // handle logic
        return new Workout();
    }
}
