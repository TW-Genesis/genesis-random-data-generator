package com.genesis.datagenerator.entities;

public class Constituent {
    private final String chemicalId;
    private final Concentration concentration;

    public Constituent(String chemicalId, Concentration concentration) {
        this.chemicalId = chemicalId;
        this.concentration = concentration;
    }

    public String getChemicalId() {
        return chemicalId;
    }

    public Concentration getConcentration() {
        return concentration;
    }
}
