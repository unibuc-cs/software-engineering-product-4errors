package com._errors.MovieMingle.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MovieApiResponse {
    private List<MovieDto> results;  // Filmele din răspuns
    @JsonProperty("total_pages")
    private int totalPages;          // Totalul paginilor din răspuns
    @JsonProperty("total_results")
    private int totalResults;        // Totalul rezultatelor din răspuns

    // Getter și setter pentru results
    public List<MovieDto> getResults() {
        return results;
    }

    public void setResults(List<MovieDto> results) {
        this.results = results;
    }

    // Getter și setter pentru totalPages
    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    // Getter și setter pentru totalResults
    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
