package com.SJCFIT.trial.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class UserPreferences {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    //Simplifies one‑to‑one mappings: No need for separate surrogate keys in the child table.
    //Enforces tight coupling: Guarantees that a child cannot exist without its parent.

    private String accountId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private boolean isSubscriber;
    private String subscriberLevel;

    public UserPreferences(String accountId) {
        this.accountId = accountId;
    }
}