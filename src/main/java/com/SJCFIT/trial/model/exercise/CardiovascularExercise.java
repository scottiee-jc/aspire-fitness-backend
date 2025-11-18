package com.SJCFIT.trial.model.exercise;

import com.SJCFIT.trial.model.Intensity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardiovascularExercise extends Exercise {

    private int exerciseDuration;

    public CardiovascularExercise(String name, String description, String intensity, int exerciseDuration) {
        super(name, description, intensity);
        this.exerciseDuration = exerciseDuration;
    }
}
