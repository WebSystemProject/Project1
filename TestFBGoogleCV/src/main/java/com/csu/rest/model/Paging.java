package com.csu.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cursors"
})
public class Paging {

    @JsonProperty("cursors")
    private change cursors;

    @JsonProperty("cursors")
    public change getCursors() {
        return cursors;
    }

    @JsonProperty("cursors")
    public void setCursors(change cursors) {
        this.cursors = cursors;
    }

}