package com.SJCFIT.trial.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.concurrent.Flow;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_PREFERENCES", schema = "RECORDS")
public class UserPreferences {

    public UserPreferences(String accountId) {
        this.accountId = accountId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_PREFERENCE_ID")
    private Long id;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "SUBSCRIBER", nullable = false)
    private boolean isSubscriber;

    @Column(name = "LAST_NAME")
    private String subscriberLevel;

}