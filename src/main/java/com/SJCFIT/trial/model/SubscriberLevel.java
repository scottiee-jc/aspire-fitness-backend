package com.SJCFIT.trial.model;

public enum SubscriberLevel {

    IRON("IRON"),
    STEEL("STEEL"),
    TITANIUM("TITANIUM");

    SubscriberLevel(String value){
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
