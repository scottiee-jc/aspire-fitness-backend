package com.SJCFIT.trial.model.user;

import com.SJCFIT.trial.model.Workout;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // unique database id
    private String accountNumber; // unique
    private String firstName;
    private String lastName;
    private int age;
    private LocalDate dob;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserPreferences userPreferences;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserWorkoutPlan> userWorkoutPlans;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Workout> userSavedWorkouts;

    public User(String firstName, String lastName, LocalDate dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }
}
