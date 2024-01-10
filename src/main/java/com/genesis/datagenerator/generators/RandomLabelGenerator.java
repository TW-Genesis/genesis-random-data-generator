package com.genesis.datagenerator.generators;

import java.util.ArrayList;
import java.util.List;

public class RandomLabelGenerator {
    private final String label;
    private final int min;
    private final int max;
    private final ValueGenerator valueGenerator;

    public RandomLabelGenerator(String label, int min, int max) {
        this.label = label;
        this.min = min;
        this.max = max;
        valueGenerator = new ValueGenerator(123456L);
    }

    public String getLabel() {
        return label + valueGenerator.randomInt(min, max);
    }

    public List<String> getLabels(int size) {
        ArrayList<String> labels = new ArrayList<>();
        for (int counter = 0; counter < size; counter++) {
            labels.add(getLabel());
        }
        return labels;
    }
}
