package com.genesis.datagenerator.entities;

public class Concentration {
    private final double value;
    private final String unit;

    public Concentration(double value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
