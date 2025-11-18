package com.SJCFIT.trial.model.exercise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class WeightliftingExercise extends Exercise {

    private int sets;
    private int reps;
    private Weight weight;
    private Equipment equipment;

    public WeightliftingExercise(String name, String description, String intensity, int sets, int reps, Weight weight, Equipment equipment) {
        super(name, description, intensity);
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.equipment = equipment;
    }
}
