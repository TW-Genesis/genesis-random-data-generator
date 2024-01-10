package com.genesis.datagenerator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.List;

//TODO: null handling for all methods
public class ModelBuilder {
    static final String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
    static final String owl = "http://www.w3.org/2002/07/owl#";
    static final String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    static final String xsd = "http://www.w3.org/2001/XMLSchema#";
    private OntModel model;

    public ModelBuilder(OntModel model) {
        this.model = ModelFactory.createOntologyModel();
        this.model.add(model);
    }

    public OntModel build() {
        return model;
    }

    public OntClass getClassHavingLabel(String classLabel) {
        return model.getOntClass(getTermIRI(classLabel));
    }

    public Property getPropertyHavingLabel(String propertyLabel) {
        return model.getProperty(getTermIRI(propertyLabel));
    }

    public Individual getIndividualHavingLabel(String individualLabel) {
        return model.getIndividual(getTermIRI(individualLabel));
    }

    public Resource getResourceObject(Resource subject, String propertyLabel) {
        return subject.getPropertyResourceValue(getPropertyHavingLabel(propertyLabel));
    }

    public Resource getSubject(Resource object, String propertyLabel) {
        StmtIterator iter = model.listStatements(new SimpleSelector(null, getPropertyHavingLabel(propertyLabel), object));
        if (iter.hasNext()) {
            return iter.nextStatement().getSubject();
        } else {
            throw new RuntimeException("no matching statements found!");
        }
    }

    public Individual getFirstIndividualOfClass(String classLabel) {
        ExtendedIterator<Individual> experimentDesigns = getIndividuals(classLabel);
        Individual experimentDesign;
        if (experimentDesigns.hasNext()) {
            experimentDesign = experimentDesigns.next();
        } else {
            throw new RuntimeException("no individual for " + classLabel + " class found!");
        }
        return experimentDesign;
    }

    public Resource addIndividualObject(Resource subject, String propertyLabel, RDFNode object) {
        return subject.addProperty(getPropertyHavingLabel(propertyLabel), object);
    }

    public Individual createIndividual(String individualClassLabel) {
        return model.createIndividual(getClassHavingLabel(individualClassLabel));
    }

    public ExtendedIterator<Individual> getIndividuals(String individualClassLabel) {
        return model.listIndividuals(getClassHavingLabel(individualClassLabel));
    }

    public Individual createIndividual(String individualIRI, String individualClassLabel) {
        return model.createIndividual(individualIRI, getClassHavingLabel(individualClassLabel));
    }

    public void createSubclass(String subclassIRI, String label, String parentClassLabel) {
//        System.out.println("hi there");
        OntClass subClass = model.createClass(subclassIRI);
        subClass.addLabel(model.createLiteral(label));
        subClass.addSuperClass(getClassHavingLabel(parentClassLabel));
//        System.out.println("bye there");

    }


    private String getTermIRI(String termLabel) {
        String classIRI;
        StmtIterator iter = model.listStatements(new SimpleSelector(null, model.getProperty(rdfs + "label"), termLabel));
        if (iter.hasNext()) {
            classIRI = iter.nextStatement().getSubject().getURI();
        } else {
            throw new RuntimeException("no entity with label [" + termLabel + "] found in the ontology");
        }
        return classIRI;
    }

    public RDFList getFirstRDFListObject(Individual subject, String propertyLabel) {
        Resource listHead = subject.getPropertyResourceValue(getPropertyHavingLabel(propertyLabel));
        return model.getList(listHead);
    }

    public void addListObject(Individual subject, String propertyLabel, List<Individual> individualList) {
        model.add(subject,
                getPropertyHavingLabel(propertyLabel),
                model.createList(individualList.listIterator()));
    }
}
