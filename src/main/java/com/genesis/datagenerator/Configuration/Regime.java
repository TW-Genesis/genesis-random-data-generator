package com.genesis.datagenerator.Configuration;

public class Regime {
    private MinMax temperature;
    private MinMax flowRate;

    private GrowthMediumComposition growthMediumComposition;
    private int dictionarySize;

    private String iriPrefix;

    public String getIriPrefix() {
        return iriPrefix;
    }

    public void setIriPrefix(String iriPrefix) {
        this.iriPrefix = iriPrefix;
    }

    public GrowthMediumComposition getGrowthMediumComposition() {
        return growthMediumComposition;
    }

    public void setGrowthMediumComposition(GrowthMediumComposition growthMediumComposition) {
        this.growthMediumComposition = growthMediumComposition;
    }

    public MinMax getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(MinMax flowRate) {
        this.flowRate = flowRate;
    }

    public MinMax getTemperature() {
        return temperature;
    }

    public void setTemperature(MinMax temperature) {
        this.temperature = temperature;
    }

    public int getDictionarySize() {
        return dictionarySize;
    }

    public void setDictionarySize(int dictionarySize) {
        this.dictionarySize = dictionarySize;
    }
}
