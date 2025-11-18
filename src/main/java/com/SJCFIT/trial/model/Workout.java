package com.SJCFIT.trial.model;

import com.SJCFIT.trial.model.exercise.Exercise;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Stack;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workouts")
@NamedQuery(
        name = "Employee.byDepartment",
        query = "FROM Employee WHERE department = :department",
        resultClass = Workout.class
)
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @ManyToOne(targetEntity = Exercise.class)
    @JoinColumn(name = "EXERCISE_LIST")
    private Stack<Exercise> exerciseList;
}
