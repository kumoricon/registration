package org.kumoricon.registration.inlinereg.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InLineRegistrationRecord {
    private final Integer id;
    private final String created;
    private final String data;
    private final String confirmationCode;

    @JsonCreator
    public InLineRegistrationRecord(@JsonProperty(value = "id") Integer id,
                                    @JsonProperty(value = "created") String created,
                                    @JsonProperty(value = "data") String data,
                                    @JsonProperty(value = "confirmationCode") String confirmationCode) {
        this.id = id;
        this.created = created;
        this.data = data;
        this.confirmationCode = confirmationCode;
    }

    public Integer getId() { return id; }

    public String getCreated() {
        return created;
    }

    public String getData() {
        return data;
    }

    public String getConfirmationCode() { return confirmationCode; }
}
