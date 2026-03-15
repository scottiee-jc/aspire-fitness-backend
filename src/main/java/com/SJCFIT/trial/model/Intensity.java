package com.SJCFIT.trial.model;

public enum Intensity {

    LIGHT("LIGHT"),
    MODERATE("MODERATE"),
    HIGH("HIGH"),
    ALLOUT("ALL-OUT");


    private String value;

    Intensity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
