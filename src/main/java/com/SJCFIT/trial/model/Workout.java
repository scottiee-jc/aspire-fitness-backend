package com.SJCFIT.trial.model;

import com.SJCFIT.trial.model.exercise.Exercise;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Inheritance(strategy = InheritanceType.JOINED) // JOINS subclasses to parent table
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Blob picture;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private com.SJCFIT.trial.model.user.User user;

    @ManyToOne
    @JoinColumn(name = "workout_plan_id")
    private com.SJCFIT.trial.model.user.UserWorkoutPlan userWorkoutPlan;

    @OneToMany(targetEntity = Exercise.class)
    @JoinColumn(name = "workout_id")
    private List<Exercise> exerciseList = new ArrayList<>();

    public Workout(Long id, String name, String description, List<Exercise> exerciseList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.exerciseList = exerciseList;
    }
}
