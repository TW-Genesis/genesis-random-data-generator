package com.genesis.datagenerator.generators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.genesis.datagenerator.Configuration.CellCulturing;
import com.genesis.datagenerator.Configuration.ExperimentConfig;
import com.genesis.datagenerator.ModelBuilder;
import com.genesis.datagenerator.dictonaries.GeneDictionary;
import com.genesis.datagenerator.dictonaries.MetaboliteDictionary;
import com.genesis.datagenerator.dictonaries.StrainDictionary;
import com.genesis.datagenerator.entities.Concentration;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@TODO: recheck all debug messages
//@TODO: convert functions to private
//@TODO: fix resource vs individuals

public class ExperimentGenerator {
    private ModelBuilder modelBuilder;
    private final ExperimentConfig experimentConfig;
    private final StrainDictionary strainDictionary;
    private final ValueGenerator valueGenerator;
    private final OntModel ontology, baseKnowledge;
    private final GeneDictionary geneDictionary;
    private final MetaboliteDictionary metaboliteDictionary;

    public ExperimentGenerator(OntModel ontology, Model regimeDictionary) throws IOException {

        File experimentConfigfile = new File(Objects.requireNonNull(ExperimentGenerator.class.getClassLoader().getResource("config.yaml")).getFile());
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        experimentConfig = objectMapper.readValue(experimentConfigfile, ExperimentConfig.class);
        strainDictionary = new StrainDictionary(experimentConfig.getStrain().getDictionarySize());
        valueGenerator = new ValueGenerator(1234567L);
        geneDictionary = new GeneDictionary();
        this.ontology = ontology;
        baseKnowledge = ModelFactory.createOntologyModel();
        baseKnowledge.add(ontology);
        baseKnowledge.add(regimeDictionary);
        metaboliteDictionary = new MetaboliteDictionary();
    }

    public OntModel getExtendedOntology() {
        return ontology;
    }


    public Model generate() {
        modelBuilder = new ModelBuilder(baseKnowledge);
        Individual experimentDesign = modelBuilder.createIndividual("study design");
        //@TODO
        Individual yeastIndividual = modelBuilder.createIndividual("Strain" + strainDictionary.getRandomStrain());
        //        Individual yeast = modelBuilder.createIndividual(strainDictionary.getRandomStrain(), "yeast");
        modelBuilder.addIndividualObject(experimentDesign, "is study of", yeastIndividual);
        createExperimentDesignLayer();
        createExperimentExecutionLayer();
        createMeasurementLayer();
        return modelBuilder.build().getRawModel().remove(ontology);
    }

    private void createExperimentDesignLayer() {
        createExperimentalRegimesLayer();
        createDurationsForExperimentalRegimes();
        createSamplingTimesForGivenRegimes();
    }

    private void createExperimentalRegimesLayer() {
        GrowthMediumCompositionGenerator growthMediumCompositionGenerator = new GrowthMediumCompositionGenerator();
        Individual experimentDesign = modelBuilder.getFirstIndividualOfClass("study design");

        int numberOfRegimes = valueGenerator.randomInt((int) experimentConfig.getExperimentDesign().getNoOfRegimes().getMin(),
                (int) experimentConfig.getExperimentDesign().getNoOfRegimes().getMax());
        List<Individual> cellCulturingRegimes = modelBuilder.getIndividuals("cell culturing regime").toList();
        List<Individual> regimeIndividuals = new ArrayList<>();
        for (int i = 0; i < numberOfRegimes; i++) {
            regimeIndividuals.add(cellCulturingRegimes.get(valueGenerator.randomInt(1, cellCulturingRegimes.size()) - 1));
        }
        modelBuilder.addListObject(experimentDesign, "has stated regimes", regimeIndividuals);
    }

    private void createDurationsForExperimentalRegimes() {
        Individual experimentDesign = modelBuilder.getFirstIndividualOfClass("study design");
        RDFList regimeList = modelBuilder.getFirstRDFListObject(experimentDesign, "has stated regimes");

        List<Individual> regimeDurations = new ArrayList<>();
        for (int i = 0; i < regimeList.size(); i++) {
            Resource regime = regimeList.get(i).asResource();
            CellCulturing cellCulturingConfig = experimentConfig.getExperimentDesign().getCellCulturing();
            Individual durationDatum = createNumericDatum(
                    "cell culturing duration datum",
                    "time value specification",
                    valueGenerator.randomInt((int) cellCulturingConfig.getDuration().getMin(), (int) cellCulturingConfig.getDuration().getMax()),
                    "hour");
            modelBuilder.addIndividualObject(durationDatum, "is about", regime);
            regimeDurations.add(durationDatum);
        }
        modelBuilder.addListObject(experimentDesign, "has stated regime durations", regimeDurations);
    }

    private void createSamplingTimesForGivenRegimes() {
        Individual experimentDesign = modelBuilder.getFirstIndividualOfClass("study design");

        RDFList regimeList = modelBuilder.getFirstRDFListObject(experimentDesign, "has stated regimes");
        RDFList regimeDurationList = modelBuilder.getFirstRDFListObject(experimentDesign, "has stated regime durations");

        List<Individual> samplingTimings = new ArrayList<>();
        for (int i = 0; i < regimeList.size(); i++) {
            int numberOfSamples = valueGenerator.randomInt((int) experimentConfig.getExperimentDesign().getSamplesTakenPerRegime().getMin(),
                    (int) experimentConfig.getExperimentDesign().getSamplesTakenPerRegime().getMax());

            Resource regimeDurationDatum = regimeDurationList.get(i).asResource();
            Resource durationValueSpec = modelBuilder.getResourceObject(regimeDurationDatum, "has value specification");
            double regimeDuration = durationValueSpec.getProperty(modelBuilder.getPropertyHavingLabel("has specified numeric value")).getObject().asLiteral().getFloat();

            List<Individual> samplingTimingsForGivenRegime = getNSamplingTimingsForGivenRegimeDuration(numberOfSamples, regimeDuration);
            for (Individual samplingTimeDatum : samplingTimingsForGivenRegime) {
                modelBuilder.addIndividualObject(samplingTimeDatum, "is about", regimeList.get(i));
            }
            samplingTimings.addAll(samplingTimingsForGivenRegime);
        }

        modelBuilder.addListObject(experimentDesign, "has stated sampling timings", samplingTimings);
    }

    private List<Individual> getNSamplingTimingsForGivenRegimeDuration(int N, double regimeDuration) {
        List<Individual> samplingTimings = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            double samplingTime = valueGenerator.randomDouble(0, regimeDuration);
            Individual samplingTimeDatum = createNumericDatum("sampling time datum", "time value specification", samplingTime, "hour");
            samplingTimings.add(samplingTimeDatum);
        }
        return samplingTimings;
    }

//    private float getTotalExperimentDuration(RDFList regimeDurationList) {
//        float totalExperimentDuration = 0;
//        for (int i = 0; i < regimeDurationList.size(); i++) {
//            Resource regimeDuration = regimeDurationList.get(i).asResource();
//            Resource durationValueSpec = modelBuilder.getResourceObject(regimeDuration, "has value specification");
//            totalExperimentDuration += durationValueSpec.getProperty(modelBuilder.getPropertyHavingLabel("has specified numeric value")).getObject().asLiteral().getFloat();
//        }
//        return totalExperimentDuration;
//    }

    private void createExperimentExecutionLayer() {

        Individual microplateWell = modelBuilder.createIndividual("Microplate Well");
        Individual parentCcpProcess = modelBuilder.createIndividual("cell culturing");

        Individual experimentDesign = modelBuilder.getFirstIndividualOfClass("study design");

        Resource yeast = modelBuilder.getResourceObject(experimentDesign, "is study of");
        modelBuilder.addIndividualObject(parentCcpProcess, "has specified yeast", yeast);
        //TODO Add has specified microplate well property in ontology and use here
        modelBuilder.addIndividualObject(parentCcpProcess, "has specified microplate well", microplateWell);

        RDFList regimeList = modelBuilder.getFirstRDFListObject(experimentDesign, "has stated regimes");

        List<Individual> childCcpProcesses = new ArrayList<>();
        for (int i = 0; i < regimeList.size(); i++) {
            Resource regime = regimeList.get(i).asResource();

            Resource temperatureDatum = modelBuilder.getResourceObject(regime, "has stated temperature");
            Individual temperatureQuality = modelBuilder.createIndividual("temperature");
            modelBuilder.addIndividualObject(temperatureDatum, "is quality measurement of", temperatureQuality);
            modelBuilder.addIndividualObject(microplateWell, "bearer of", temperatureQuality);

            Resource flowRateDatum = modelBuilder.getResourceObject(regime, "has stated flow rate");
            Resource growthMedium = modelBuilder.getResourceObject(regime, "has stated growth medium");

            Individual flowRateQuality = modelBuilder.createIndividual("fluid flow rate");
            modelBuilder.addIndividualObject(flowRateDatum, "is quality measurement of", flowRateQuality);
            modelBuilder.addIndividualObject(growthMedium, "bearer of", flowRateQuality);

            Individual childCcpProcess = modelBuilder.createIndividual("cell culturing");
            modelBuilder.addIndividualObject(childCcpProcess, "has specified growth medium", growthMedium);
            modelBuilder.addIndividualObject(childCcpProcess, "has specified regime", regime);

            childCcpProcesses.add(childCcpProcess);
        }

        modelBuilder.addListObject(parentCcpProcess, "is collection of", childCcpProcesses);
    }

    private void createMeasurementLayer() {
        createSamplingLayer();
        createTranscriptomicsLayer();
//        createMassSpecLayer();
    }

    private void createSamplingLayer() {
        Individual experimentDesign = modelBuilder.getFirstIndividualOfClass("study design");

        Resource yeastInd = modelBuilder.getResourceObject(experimentDesign, "is study of");


        Individual microplateWell = modelBuilder.getFirstIndividualOfClass("Microplate Well");

        RDFList samplingTimingsList = modelBuilder.getFirstRDFListObject(experimentDesign, "has stated sampling timings");
        for (int i = 0; i < samplingTimingsList.size(); i++) {
            Resource samplingTimingDatum = samplingTimingsList.get(i).asResource();
            Individual samplingInd = modelBuilder.createIndividual("material sampling process");
            modelBuilder.addIndividualObject(samplingInd, "has specified sampling time", samplingTimingDatum);
            Resource regimeInd = modelBuilder.getResourceObject(samplingTimingDatum, "is about");
            Resource ccpInd = modelBuilder.getSubject(regimeInd, "has specified regime");
            modelBuilder.addIndividualObject(samplingInd, "part of", ccpInd);
            modelBuilder.addIndividualObject(samplingInd, "has_specified_input", microplateWell);
            Individual materialSample = modelBuilder.createIndividual("material sample");
            modelBuilder.addIndividualObject(materialSample, "has part", yeastInd);
            modelBuilder.addIndividualObject(samplingInd, "has_specified_output", materialSample);
        }
    }

    private void createTranscriptomicsLayer() {
        Individual experimentDesign = modelBuilder.getFirstIndividualOfClass("study design");
        Resource yeast = modelBuilder.getResourceObject(experimentDesign, "is study of");
        List<Individual> materialSamples = modelBuilder.getIndividuals("material sample").toList();
        if (materialSamples.isEmpty()) {
            throw new RuntimeException("no entity for material sample found!");
        }
        for (Resource materialSampleInd : materialSamples) {
            Individual transcriptomicsInd = modelBuilder.createIndividual("Transcriptomics");
            modelBuilder.addIndividualObject(transcriptomicsInd, "has_specified_input", materialSampleInd);
            Individual measurementDataSet = modelBuilder.createIndividual("data set");
            modelBuilder.addIndividualObject(transcriptomicsInd, "has_specified_output", measurementDataSet);
            Individual genomeStructure = modelBuilder.createIndividual("genome structure");
            modelBuilder.addIndividualObject(genomeStructure, "quality of", yeast);
            measurementDataSet.addProperty(modelBuilder.getPropertyHavingLabel("is quality measurement of"), genomeStructure);
            for (int i = 0; i < geneDictionary.getDictionarySize(); i++) {
                Individual geneInd = modelBuilder.createIndividual("Gene" + geneDictionary.getGene(i));
                Individual geneCountDatum = createGeneCountDatum(valueGenerator.randomInt(experimentConfig.getMinGeneCount(), experimentConfig.getMaxGeneCount()));
                modelBuilder.addIndividualObject(geneCountDatum, "is about", geneInd);
                modelBuilder.addIndividualObject(yeast, "has part", geneInd);
                modelBuilder.addIndividualObject(measurementDataSet, "has member", geneCountDatum);
            }
        }
    }

    public Individual createGeneCountDatum(int geneCount) {
        Individual geneCountDatum = modelBuilder.createIndividual("gene count datum");
        Individual geneCountValueSpecification = modelBuilder.createIndividual("gene count value specification");

        modelBuilder.addIndividualObject(geneCountDatum, "has value specification", geneCountValueSpecification);
        geneCountValueSpecification
                .addLiteral(modelBuilder.getPropertyHavingLabel("has specified numeric value"), geneCount);
        return geneCountDatum;
    }

    private Individual instantiateConcentrationDatum(Concentration concentration) {
        return createNumericDatum("concentration of datum", "concentration value specification", concentration.getValue(), concentration.getUnit());
    }

    private Individual createNumericDatum(String datumTypeLabel, String valueSpecificationTypeLabel, double numericValue, String unitLabel) {
        Individual datum = modelBuilder.createIndividual(datumTypeLabel);
        Individual valueSpec = modelBuilder.createIndividual(valueSpecificationTypeLabel);

        modelBuilder.addIndividualObject(datum, "has value specification", valueSpec);
        modelBuilder.addIndividualObject(valueSpec, "has measurement unit label", modelBuilder.getIndividualHavingLabel(unitLabel));
        valueSpec.addLiteral(modelBuilder.getPropertyHavingLabel("has specified numeric value"), numericValue);
        return datum;
    }

}
