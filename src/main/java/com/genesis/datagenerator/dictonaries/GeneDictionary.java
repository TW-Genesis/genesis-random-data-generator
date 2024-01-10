package com.genesis.datagenerator.dictonaries;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneDictionary {
    private ArrayList<String> geneIds;
    private static final String geneDictionaryFile = "genes.txt";

    public GeneDictionary() {
        InputStream dictionary;
        try {
            dictionary = GeneDictionary.class.getClassLoader().getResourceAsStream(geneDictionaryFile);
            System.out.print("Reading in " + geneDictionaryFile + ": ");
            createGeneList(dictionary);
            assert dictionary != null;
            dictionary.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public int getDictionarySize() {
        return geneIds.size();
    }

    public String getGene(int index) {
        return geneIds.get(index);
    }

    public List<String> getRandomGenes(int noOfGenes) {
        Random random = new Random();
        int fromIndex = random.nextInt(geneIds.size() - noOfGenes);
        return geneIds.subList(fromIndex, fromIndex + noOfGenes);
    }

    //Generates a Vector of words
    @SuppressWarnings("fallthrough")
    private void createGeneList(InputStream dictionary) {
        geneIds = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dictionary));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                geneIds.add(line.strip());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(geneIds.size() + " words read in.");
    }

}
