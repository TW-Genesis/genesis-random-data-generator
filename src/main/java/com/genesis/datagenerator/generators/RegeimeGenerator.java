package com.genesis.datagenerator.generators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.genesis.datagenerator.Configuration.ExperimentConfig;
import com.genesis.datagenerator.Configuration.GrowthMediumComposition;
import com.genesis.datagenerator.ModelBuilder;
import com.genesis.datagenerator.entities.Concentration;
import com.genesis.datagenerator.entities.Constituent;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class RegeimeGenerator {
    private ModelBuilder modelBuilder;

    private final ValueGenerator valueGenerator;
    private final ExperimentConfig experimentConfig;

    private final OntModel ontology;

    public RegeimeGenerator(OntModel ontology) throws IOException {
        InputStream experimentConfigfile = Objects.requireNonNull(ExperimentGenerator.class.getClassLoader().getResourceAsStream("config.yaml"));
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        experimentConfig = objectMapper.readValue(experimentConfigfile, ExperimentConfig.class);

        this.ontology = ontology;

        valueGenerator = new ValueGenerator(1234567L);
    }

    public Model generate() {
        modelBuilder = new ModelBuilder(ontology);
        GrowthMediumCompositionGenerator growthMediumCompositionGenerator = new GrowthMediumCompositionGenerator();
        for (int i = 1; i <= experimentConfig.getRegime().getDictionarySize(); i++) {
            Individual regime = modelBuilder.createIndividual(experimentConfig.getRegime().getIriPrefix() + i, "cell culturing regime");
            double minimumTemp = experimentConfig.getRegime().getTemperature().getMin();
            double maximumTemp = experimentConfig.getRegime().getTemperature().getMax();
            Individual temperatureDatum = createNumericDatum("temperature datum", "temperature value specification", valueGenerator.randomDouble(minimumTemp, maximumTemp), "degree Celsius");
            modelBuilder.addIndividualObject(regime, "has stated temperature", temperatureDatum);
            double minimumFlowRate = experimentConfig.getRegime().getFlowRate().getMin();
            double maximumFlowRate = experimentConfig.getRegime().getFlowRate().getMax();
            Individual flowRateDatum = createNumericDatum("volumetric flow rate datum", "volumetric flow rate value specification", valueGenerator.randomDouble(minimumFlowRate, maximumFlowRate), "Milliliter per Hour");
            modelBuilder.addIndividualObject(regime, "has stated flow rate", flowRateDatum);
            Individual growthMedium = modelBuilder.createIndividual("Growth Medium");
            Individual growthMediumComposition = modelBuilder.createIndividual("Growth Medium composition");
            modelBuilder.addIndividualObject(regime, "has stated composition", growthMediumComposition);
            GrowthMediumComposition growthMediumCompositionConfig = experimentConfig.getRegime().getGrowthMediumComposition();
            List<Constituent> constituents = growthMediumCompositionGenerator.getRandomConstituents(
                    valueGenerator.randomInt(
                            growthMediumCompositionConfig.getMinNumberOfConstituents(),
                            growthMediumCompositionConfig.getMaxNumberOfConstituents()),
                    growthMediumCompositionConfig.getMinimumConcentrationPerConstituent(),
                    growthMediumCompositionConfig.getMaximumConcentrationPerConstituent());
            for (Constituent constituent : constituents) {
                Individual chemical = modelBuilder.createIndividual("Metabolite"+constituent.getChemicalId());
                modelBuilder.addIndividualObject(growthMedium, "has part", chemical);
                Individual concentrationDatum = instantiateConcentrationDatum(constituent.getConcentration());
                modelBuilder.addIndividualObject(growthMediumComposition, "has stated concentration", concentrationDatum);
                modelBuilder.addIndividualObject(chemical, "has concentration", concentrationDatum);
            }
            modelBuilder.addIndividualObject(regime, "has stated growth medium", growthMedium);
        }
        return modelBuilder.build().getRawModel().remove(ontology);
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
