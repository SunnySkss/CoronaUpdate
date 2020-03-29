package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class CoronaPojo {

    /**
     * best_guess :
     * links : []
     * status_code : 200
     * similar_images : []
     * titles : ["318,254 ","13,671","96,010"]
     * descriptions : []
     */

    private String best_guess;
    private String status_code;
    private List<?> links;
    private List<?> similar_images;
    private List<String> titles;
    private List<?> descriptions;

    public ArrayList<?> getCorona() {
        return corona;
    }

    public void setCorona(ArrayList<?> corona1) {
        this.corona = corona1;
    }

    private ArrayList<?>corona;

    public List<String> getCorona_cases() {
        return corona_cases;
    }

    public void setCorona_cases(List<String> corona_cases) {
        this.corona_cases = corona_cases;
    }

    private List<String> corona_cases;

    public ArrayList<String> getCountry_list() {
        return country_list;
    }

    public void setCountry_list(ArrayList<String> country_list) {
        this.country_list = country_list;
    }

    private ArrayList<String> country_list;


    public String getBest_guess() {
        return best_guess;
    }

    public void setBest_guess(String best_guess) {
        this.best_guess = best_guess;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public List<?> getLinks() {
        return links;
    }

    public void setLinks(List<?> links) {
        this.links = links;
    }

    public List<?> getSimilar_images() {
        return similar_images;
    }

    public void setSimilar_images(List<?> similar_images) {
        this.similar_images = similar_images;
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<?> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<?> descriptions) {
        this.descriptions = descriptions;
    }
}
