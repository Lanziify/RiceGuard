package com.pyu.riceleafdiseasedetection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Disease {
    private int prediction;
    private String disease;
    private String description;
    private String causesHeading;
    private String recommendationHeading;
    private List<Causes> causes;
    private List<Recommendations> recommendations;

    public Disease() {
    }

    public int getPrediction() {
        return prediction;
    }

    public void setPrediction(int prediction) {
        this.prediction = prediction;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCausesHeading() {
        return causesHeading;
    }

    public void setCausesHeading(String causesHeading) {
        this.causesHeading = causesHeading;
    }

    public String getRecommendationHeading() {
        return recommendationHeading;
    }

    public void setRecommendationHeading(String recommendationHeading) {
        this.recommendationHeading = recommendationHeading;
    }

    public List<Causes> getCauses() {
        return causes;
    }

    public void setCauses(List<Causes> causes) {
        this.causes = causes;
    }

    public List<Recommendations> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendations> recommendations) {
        this.recommendations = recommendations;
    }
}
