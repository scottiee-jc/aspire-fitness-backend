package com.SJCFIT.trial.model;

import com.SJCFIT.trial.model.exercise.Exercise;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.util.Stack;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // JOINS subclasses to parent table
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Blob picture;

    @OneToMany(targetEntity = Exercise.class)
    @JoinColumn(name = "EXERCISE_LIST")
    private Stack<Exercise> exerciseList;

    public Workout(Long id, String name, String description, Stack<Exercise> exerciseList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.exerciseList = exerciseList;
    }
}
