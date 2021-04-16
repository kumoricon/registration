package org.kumoricon.registration.inlinereg.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InLineRegistrationFile {
    private final Boolean success;
    private final List<InLineRegistrationRecord> data;
    private final String message;

    @JsonCreator
    public InLineRegistrationFile(@JsonProperty(value="success", required = true) Boolean success,
                                  @JsonProperty(value="data", required = true) List<InLineRegistrationRecord> data,
                                  @JsonProperty(value="message") String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public List<InLineRegistrationRecord> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
