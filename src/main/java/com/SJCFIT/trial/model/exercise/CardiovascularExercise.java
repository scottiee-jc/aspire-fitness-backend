package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CardiovascularExercise extends Exercise {

    private int exerciseDuration;

    public CardiovascularExercise(Long id, String name, String description, String intensity, int exerciseDuration) {
        super(id, name, description, intensity);
        this.exerciseDuration = exerciseDuration;
    }
}
