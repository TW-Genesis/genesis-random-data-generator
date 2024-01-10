package com.genesis.datagenerator.Configuration;

public class GrowthMediumComposition {
    private int minNumberOfConstituents;
    private int maxNumberOfConstituents;
    private double minimumConcentrationPerConstituent;
    private double maximumConcentrationPerConstituent;

    public int getMaxNumberOfConstituents() {
        return maxNumberOfConstituents;
    }

    public void setMaxNumberOfConstituents(int maxNumberOfConstituents) {
        this.maxNumberOfConstituents = maxNumberOfConstituents;
    }

    public int getMinNumberOfConstituents() {
        return minNumberOfConstituents;
    }

    public void setMinNumberOfConstituents(int minNumberOfConstituents) {
        this.minNumberOfConstituents = minNumberOfConstituents;
    }

    public double getMinimumConcentrationPerConstituent() {
        return minimumConcentrationPerConstituent;
    }

    public void setMinimumConcentrationPerConstituent(double minimumConcentrationPerConstituent) {
        this.minimumConcentrationPerConstituent = minimumConcentrationPerConstituent;
    }

    public double getMaximumConcentrationPerConstituent() {
        return maximumConcentrationPerConstituent;
    }

    public void setMaximumConcentrationPerConstituent(double maximumConcentrationPerConstituent) {
        this.maximumConcentrationPerConstituent = maximumConcentrationPerConstituent;
    }
}
