package com.SJCFIT.trial.model.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BodyweightExercise extends Exercise {

    private int sets;
    private int reps;

    public BodyweightExercise(String name, String description, String intensity, int sets, int reps) {
        super(name, description, intensity);
        this.sets = sets;
        this.reps = reps;
    }
}
