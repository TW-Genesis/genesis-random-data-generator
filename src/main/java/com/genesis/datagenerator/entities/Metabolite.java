package com.genesis.datagenerator.entities;

public class Metabolite {
    private final String id;
    private final String name;
    private final Double mass;

    public Metabolite(String id, String name, Double mass) {
        this.id = id;
        this.name = name;
        this.mass = mass;
    }

    public String getName() {
        return name;
    }

    public Double getMass() {
        return mass;
    }

    public String getId() {
        return id;
    }
}
