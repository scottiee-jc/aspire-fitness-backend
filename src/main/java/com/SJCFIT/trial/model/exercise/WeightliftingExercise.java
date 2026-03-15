package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class WeightliftingExercise extends Exercise {

    private int sets;
    private int reps;

    @Embedded
    private Weight weight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "equipment_name")),
            @AttributeOverride(name = "descriptionOfUse", column = @Column(name = "equipment_description"))
    })
    private Equipment equipment;

    public WeightliftingExercise(Long id, String name, String description, String intensity, int sets, int reps, Weight weight, Equipment equipment) {
        super(id, name, description, intensity);
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.equipment = equipment;
    }
}
