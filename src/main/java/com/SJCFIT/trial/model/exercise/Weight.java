package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.Embeddable;

@Embeddable
public record Weight (
        int weight,
        String unit
){}
