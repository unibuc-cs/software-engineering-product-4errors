package com._errors.MovieMingle.dto;

public class TrailerApiResult {
    private String key;
    private String type; // Adaugă atributul pentru tipul trailerului
    private String site; // Adaugă atributul pentru site-ul video

    // Getter și Setter pentru key
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getter și Setter pentru type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter și Setter pentru site
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
