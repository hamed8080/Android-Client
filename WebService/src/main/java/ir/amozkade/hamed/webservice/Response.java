package ir.amozkade.hamed.webservice;

import org.json.JSONArray;

/**
 * Created by hamed on 7/28/2017.
 */

public class Response {
    public  String message;
    public JSONArray data ;
    public int responseCode ;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
