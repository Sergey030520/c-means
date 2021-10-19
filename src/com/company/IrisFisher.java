package com.company;

import java.util.HashMap;

public class IrisFisher {
    private HashMap<String, Double> features = new HashMap<>();
    private String species = "";

    IrisFisher() {}
    IrisFisher(HashMap<String, Double> inFeatures){
        features = inFeatures;
    }
    public void setFeatures(HashMap<String, Double> features) {
        this.features = features;
    }


    public HashMap<String, Double> getFeatures() {
        return this.features;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSpecies() {
        return this.species;
    }
}
