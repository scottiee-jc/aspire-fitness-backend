package com.SJCFIT.trial.model;

import com.SJCFIT.trial.model.user.User;
import com.SJCFIT.trial.model.user.UserWorkoutPlan;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;


/**
 * Will be accessible only to specific users. A private
 */

@Entity
@Getter
@Setter
public class PrivateUserWorkout extends Workout {

    @ManyToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserWorkoutPlan userWorkoutPlan;

}
