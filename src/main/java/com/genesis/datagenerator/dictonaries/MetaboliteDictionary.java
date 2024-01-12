package com.genesis.datagenerator.dictonaries;

import com.genesis.datagenerator.entities.Metabolite;
import com.genesis.datagenerator.generators.ValueGenerator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MetaboliteDictionary {
    private ArrayList<Metabolite> metabolites = new ArrayList<>();

    private final ValueGenerator valueGenerator;
    private static final String filePath = "metabolite.csv";

    public MetaboliteDictionary() {
        try {
            InputStream inputStream = MetaboliteDictionary.class.getClassLoader().getResourceAsStream(filePath);

            BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
            CSVParser csvParser = new CSVParser(fileReader, CSVFormat.Builder.create().setHeader().build());
            List<CSVRecord> csvRecords = csvParser.getRecords();
            csvRecords.forEach(record -> {
                String metaboliteId = record.get("pubchem_cid");
                String metaboliteName = record.get("metaboliteName");
                Double exactMass = Double.parseDouble(record.get("exactMass"));
                metabolites.add(new Metabolite(metaboliteId, metaboliteName, exactMass));
            });

        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        valueGenerator = new ValueGenerator(2434L);
    }

    public int getDictionarySize() {
        return metabolites.size();
    }

    public Metabolite getRandomMetabolite() {
        return metabolites.get(valueGenerator.randomInt(0, getDictionarySize()));
    }

    public Metabolite getMetabolite(int i){
        return metabolites.get(i);
    }

}
