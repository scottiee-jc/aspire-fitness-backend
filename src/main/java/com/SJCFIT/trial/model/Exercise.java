package com.SJCFIT.trial.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "exercise_id")
    private Long id;

    @Column
    private String name;

    private int sets;

    private int reps;

    private String description;

    public Exercise(String name) {
        this.name = name;
    }
}
