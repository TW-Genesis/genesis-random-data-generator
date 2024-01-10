package com.genesis.datagenerator.dictonaries;

import java.util.Random;

public class StrainDictionary {
    private final int dictionarySize;
    private final Random random;

    public StrainDictionary(int dictionarySize) {
        this.dictionarySize = dictionarySize;
        this.random = new Random();
    }

    public String getStrain(int index){
        if((index+1) > dictionarySize  || index < 0)
            throw new RuntimeException("invalid strain index!");
        return "C" + (1000 + index);
    }

    public String getRandomStrain() {
        return "C" + (1000 + random.nextInt(dictionarySize));
    }
}
