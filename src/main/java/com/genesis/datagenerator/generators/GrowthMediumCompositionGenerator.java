package com.genesis.datagenerator.generators;

import com.genesis.datagenerator.dictonaries.MetaboliteDictionary;
import com.genesis.datagenerator.entities.Concentration;
import com.genesis.datagenerator.entities.Constituent;

import java.util.ArrayList;
import java.util.List;

public class GrowthMediumCompositionGenerator {

    private final MetaboliteDictionary metaboliteDictionary;
    private final ValueGenerator valueGenerator;

    public GrowthMediumCompositionGenerator() {
        metaboliteDictionary = new MetaboliteDictionary();
        valueGenerator = new ValueGenerator(23423L);
    }

    public List<Constituent> getRandomConstituents(int numberOfConstituents, double minConcentrationPerConstituent, double maxConcentrationPerConstituent) {
        ArrayList<Constituent> constituents = new ArrayList<>();
        for (int i = 0; i < numberOfConstituents; i++) {
            constituents.add(new Constituent(metaboliteDictionary.getRandomMetabolite().getId(),
                    new Concentration(valueGenerator.randomDouble(minConcentrationPerConstituent, maxConcentrationPerConstituent),"gram per litre")));
        }
        return constituents;
    }
}
