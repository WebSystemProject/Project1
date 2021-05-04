package com.csu.rest.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "paging"
})
public class FbAlbums {

    @JsonProperty("data")
    private List<Datum> data = null;
    @JsonProperty("paging")
    private Paging_ paging;

    @JsonProperty("data")
    public List<Datum> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    @JsonProperty("paging")
    public Paging_ getPaging() {
        return paging;
    }

    @JsonProperty("paging")
    public void setPaging(Paging_ paging) {
        this.paging = paging;
    }

}