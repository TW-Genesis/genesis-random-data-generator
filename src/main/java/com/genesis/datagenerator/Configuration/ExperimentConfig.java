package com.genesis.datagenerator.Configuration;

public class ExperimentConfig {
    private Regime regime;
    private ExperimentDesign experimentDesign;
    private Strain strain;

    private Transcriptomics transcriptomics;

    public Transcriptomics getTranscriptomics() {
        return transcriptomics;
    }

    public void setTranscriptomics(Transcriptomics transcriptomics) {
        this.transcriptomics = transcriptomics;
    }

    public Regime getRegime() {
        return regime;
    }

    public void setRegime(Regime regime) {
        this.regime = regime;
    }

    public ExperimentDesign getExperimentDesign() {
        return experimentDesign;
    }

    public void setExperimentDesign(ExperimentDesign experimentDesign) {
        this.experimentDesign = experimentDesign;
    }

    public Strain getStrain() {
        return strain;
    }

    public void setStrain(Strain stain) {
        this.strain = stain;
    }

    public int getMinGeneCount() {
        return (int) getTranscriptomics().getGeneCount().getMin();
    }

    public int getMaxGeneCount() {
        return (int) getTranscriptomics().getGeneCount().getMax();
    }
}
