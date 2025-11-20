package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class BodyweightExercise extends Exercise {

    private int sets;
    private int reps;

    public BodyweightExercise(Long id, String name, String description, String intensity, int sets, int reps) {
        super(id, name, description, intensity);
        this.sets = sets;
        this.reps = reps;
    }
}
