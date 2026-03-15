package com.SJCFIT.trial.model.exercise;

import jakarta.persistence.Embeddable;

@Embeddable
public record Equipment (
        String name,
        String descriptionOfUse
){ }
