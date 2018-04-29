package ir.amozkade.hamed.webservice;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hamed on 7/28/2017
 */

public class Response {
    @JsonProperty("Message")
    private   String message;
    @JsonProperty("Data")
    private String data ;
    @JsonProperty("ResponseCode")
    private int responseCode ;

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getData() {
        return data;
    }

    public Response setData(String data) {
        this.data = data;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Response setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }
}
