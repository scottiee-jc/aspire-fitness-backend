package com.SJCFIT.trial.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private int age;
    private LocalDate dob;
    private List<String> workouts;

    //controller constructor for new user
    public User(String firstName, String lastName, LocalDate dob) {
    }
}
