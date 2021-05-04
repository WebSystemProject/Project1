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
public class Photos {

    @JsonProperty("data")
    private List<Datum_> data = null;
    @JsonProperty("paging")
    private Paging paging;

    @JsonProperty("data")
    public List<Datum_> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Datum_> data) {
        this.data = data;
    }

    @JsonProperty("paging")
    public Paging getPaging() {
        return paging;
    }

    @JsonProperty("paging")
    public void setPaging(Paging paging) {
        this.paging = paging;
    }

}