package com.SJCFIT.trial.config;

import com.SJCFIT.trial.model.Workout;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;

public class Config {

//    @Produces @ApplicationScoped @Documents
    EntityManagerFactory configure() {
        return new PersistenceConfiguration("WorkoutData")
                .nonJtaDataSource("java:global/jdbc/WorkoutDatabase")
                .managedClass(Workout.class)
                .property(PersistenceConfiguration.LOCK_TIMEOUT, 5000)
                .createEntityManagerFactory();
    }
}
