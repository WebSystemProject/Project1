package com.csu.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "cursors",
    "next",
    "previous"
})
public class Paging_ {

    @JsonProperty("cursors")
    private Cursors_ cursors;
    @JsonProperty("next")
    private String next;
    @JsonProperty("previous")
    private String previous;

    @JsonProperty("cursors")
    public Cursors_ getCursors() {
        return cursors;
    }

    @JsonProperty("cursors")
    public void setCursors(Cursors_ cursors) {
        this.cursors = cursors;
    }

    @JsonProperty("next")
    public String getNext() {
        return next;
    }

    @JsonProperty("next")
    public void setNext(String next) {
        this.next = next;
    }

    @JsonProperty("previous")
    public String getPrevious() {
        return previous;
    }

    @JsonProperty("previous")
    public void setPrevious(String previous) {
        this.previous = previous;
    }

}