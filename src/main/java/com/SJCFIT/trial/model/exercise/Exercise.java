package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED) // JOINS subclasses to parent table
public abstract class Exercise {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private String intensity;
}
