package com.SJCFIT.trial.model.user;

import com.SJCFIT.trial.model.Workout;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserWorkoutPlan {

    @Id
    private Long id;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    //Simplifies one‑to‑one mappings: No need for separate surrogate keys in the child table.
    //Enforces tight coupling: Guarantees that a child cannot exist without its parent.

    private String name;

    private LocalDate date;

    @OneToMany(mappedBy = "userWorkoutPlan", cascade = CascadeType.ALL)
    private List<Workout> workoutCollection;
}
