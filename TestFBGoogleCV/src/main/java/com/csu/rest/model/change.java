package com.csu.rest.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "before",
    "after"
})
public class change {
	   @JsonProperty("before")
	    private String before;
	    @JsonProperty("after")
	    private String after;

	    @JsonProperty("before")
	    public String getBefore() {
	        return before;
	    }

	    @JsonProperty("before")
	    public void setBefore(String before) {
	        this.before = before;
	    }

	    @JsonProperty("after")
	    public String getAfter() {
	        return after;
	    }

	    @JsonProperty("after")
	    public void setAfter(String after) {
	        this.after = after;
	    }

}
