package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercises")
public abstract class Exercise {

    private String name;

    private String description;

    private String intensity;
}
