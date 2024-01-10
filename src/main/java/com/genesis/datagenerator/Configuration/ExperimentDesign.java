package com.genesis.datagenerator.Configuration;

public class ExperimentDesign {
    private MinMax noOfRegimes;
    private MinMax samplesTakenPerRegime;

    private CellCulturing cellCulturing;
    
    public CellCulturing getCellCulturing() {
        return cellCulturing;
    }

    public void setCellCulturing(CellCulturing cellCulturing) {
        this.cellCulturing = cellCulturing;
    }

    public MinMax getNoOfRegimes() {
        return noOfRegimes;
    }

    public MinMax getSamplesTakenPerRegime() {
        return samplesTakenPerRegime;
    }

    public void setNoOfRegimes(MinMax noOfRegimes) {
        this.noOfRegimes = noOfRegimes;
    }

    public void setSamplesTakenPerRegime(MinMax samplesTakenPerRegime) {
        this.samplesTakenPerRegime = samplesTakenPerRegime;
    }

}
