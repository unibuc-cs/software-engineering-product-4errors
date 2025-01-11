package com._errors.MovieMingle.dto;

import java.util.List;

public class TrailerApiResponse {
    private List<TrailerApiResult> results;

    public List<TrailerApiResult> getResults() {
        return results;
    }

    public void setResults(List<TrailerApiResult> results) {
        this.results = results;
    }

}
