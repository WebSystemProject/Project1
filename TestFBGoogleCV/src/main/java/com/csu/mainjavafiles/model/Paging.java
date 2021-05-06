package com.csu.mainjavafiles.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cursors"
})
public class Paging {

    @JsonProperty("cursors")
    private Cursors cursors;

    @JsonProperty("cursors")
    public Cursors getCursors() {
        return cursors;
    }

    @JsonProperty("cursors")
    public void setCursors(Cursors cursors) {
        this.cursors = cursors;
    }

}