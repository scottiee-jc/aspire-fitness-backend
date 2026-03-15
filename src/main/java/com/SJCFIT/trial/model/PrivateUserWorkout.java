package com.SJCFIT.trial.model;

import com.SJCFIT.trial.model.user.User;
import com.SJCFIT.trial.model.user.UserWorkoutPlan;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


/**
 * Will be accessible only to specific users. A private workout.
 */

@Entity
@Getter
@Setter
public class PrivateUserWorkout extends Workout {

    @ManyToOne
    @JoinColumn(name = "private_user_id")
    private User privateUser;

}
