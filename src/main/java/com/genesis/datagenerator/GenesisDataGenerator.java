package com.genesis.datagenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.genesis.datagenerator.Configuration.ExperimentConfig;
import com.genesis.datagenerator.dictonaries.GeneDictionary;
import com.genesis.datagenerator.dictonaries.MetaboliteDictionary;
import com.genesis.datagenerator.dictonaries.StrainDictionary;
import com.genesis.datagenerator.generators.ExperimentGenerator;
import com.genesis.datagenerator.generators.RegeimeGenerator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class GenesisDataGenerator {
    private static final String outputFilePrefix = "experiments";
    private static int experimentCount = 1;
    private static int serverNodesCount = 1;
    private static String outputDirectory = "output";

    public static void main(String[] args) throws IOException {
        initConfig(args);
        cleanOutputDirectory();

        OntModel baseOntology = ModelFactory.createOntologyModel();
        InputStream ontologyResource = Objects.requireNonNull(ExperimentGenerator.class.getClassLoader().getResourceAsStream("genesis.owl"));
        baseOntology.read(ontologyResource,null, "TTL");
        OntModel ontology = modifyOntology(baseOntology);
        Model regimeDictionary = new RegeimeGenerator(ontology).generate();
        ExperimentGenerator experimentGenerator = new ExperimentGenerator(ontology, regimeDictionary);

        OntModel extendedOntology = experimentGenerator.getExtendedOntology();
        String outputOntologyFilePath = outputDirectory + "/" + "modified-ontology" + ".nt";
        exportModelToFile(extendedOntology, outputOntologyFilePath);

        for (int experimentCounter = 1; experimentCounter <= experimentCount; experimentCounter++) {
            Model model = experimentGenerator.generate();

            Path tempFileName = Files.createTempFile("temp", "nt").getFileName();
            String outputFilePath = outputDirectory + "/" + outputFilePrefix + "-" + getFileSuffix(experimentCounter) + ".nt";

            exportModelToFile(ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, model), tempFileName.toString());
            copyContentTo(outputFilePath, tempFileName.toString(), model.numPrefixes());
            Files.delete(tempFileName);
        }
    }

    private static OntModel modifyOntology(OntModel baseOntology) throws IOException {
        ModelBuilder modelBuilder = new ModelBuilder(baseOntology);

        InputStream experimentConfigfile = Objects.requireNonNull(ExperimentGenerator.class.getClassLoader().getResourceAsStream("config.yaml"));
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        ExperimentConfig experimentConfig = objectMapper.readValue(experimentConfigfile, ExperimentConfig.class);
        StrainDictionary strainDictionary =  new StrainDictionary(experimentConfig.getStrain().getDictionarySize());
        MetaboliteDictionary metaboliteDictionary = new MetaboliteDictionary();
        GeneDictionary geneDictionary = new GeneDictionary();
        System.out.println("modifying ontology...");
        System.out.println("adding genes subclasses:");
        for (int i = 0; i < geneDictionary.getDictionarySize(); i++) {
            System.out.print("\r" + (i+1));
            modelBuilder.createSubclass("http://project-genesis.io/ontology#Gene" + geneDictionary.getGene(i), "Gene"+geneDictionary.getGene(i), "gene");
        }
        System.out.println("");

        System.out.println("adding strain subclasses:");
        for (int i = 0; i < experimentConfig.getStrain().getDictionarySize(); i++) {
            System.out.print("\r" + (i+1));
            modelBuilder.createSubclass("http://project-genesis.io/ontology#Strain" + strainDictionary.getStrain(i), "Strain" + strainDictionary.getStrain(i), "yeast");
        }
        System.out.println("");


        System.out.println("adding metabolite subclasses:");
        for (int i = 0; i < metaboliteDictionary.getDictionarySize(); i++) {
            System.out.print("\r" + (i+1));
            modelBuilder.createSubclass("http://project-genesis.io/ontology#Metabolite" + metaboliteDictionary.getMetabolite(i).getId(), "Metabolite"+metaboliteDictionary.getMetabolite(i).getId(), "molecular entity");
        }
        System.out.println("");


        return modelBuilder.build();
    }

    private static int getFileSuffix(int experimentCounter) {
        int fileSuffix = (int) Math.ceil((double) experimentCounter * serverNodesCount / experimentCount);
        if (experimentCounter == experimentCount) {
            fileSuffix = serverNodesCount;
        }
        return fileSuffix;
    }

    private static void initConfig(String[] args) {
        CommandLine cmd = CmdInputParser.getCmd(args);
        experimentCount = Integer.parseInt(cmd.getOptionValue(CmdInputParser.ec));
        if (cmd.hasOption(CmdInputParser.nodes)) {
            serverNodesCount = Integer.parseInt(cmd.getOptionValue(CmdInputParser.nodes));
        }
        if (cmd.hasOption(CmdInputParser.dir)) {
            outputDirectory = cmd.getOptionValue(CmdInputParser.dir);
        }
        System.out.println("experiment count = " + experimentCount);
        System.out.println("server node count = " + serverNodesCount);
        System.out.println("output directory = " + outputDirectory);
    }

    private static void cleanOutputDirectory() {
        try {
            FileUtils.deleteDirectory(new File(outputDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File outputDir = new File(outputDirectory);
        outputDir.mkdirs();
    }

    private static void copyContentTo(String outputFilePath, String inputFilepath, int skipLines) throws IOException {
        File inputFile = new File(inputFilepath);
        File outputFile = new File(outputFilePath);

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));

        for (int i = 0; i < skipLines; i++) {
            reader.readLine();
        }

        String line;
        while ((line = reader.readLine()) != null) {
            writer.write(line);
            writer.newLine();
        }

        reader.close();
        writer.close();
    }

    private static void exportModelToFile(OntModel ontModel, String filename) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ontModel.write(fileOutputStream, "N-TRIPLE");

    }
}
