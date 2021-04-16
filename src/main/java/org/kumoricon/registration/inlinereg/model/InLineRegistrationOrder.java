package org.kumoricon.registration.inlinereg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InLineRegistrationOrder {
    private final List<InLineRegistrationAttendee> attendees;
    private final String orderUuid;

    public InLineRegistrationOrder(
            @JsonProperty(value = "attendees", required = true) List<InLineRegistrationAttendee> attendees,
            @JsonProperty(value = "orderId", required = true) String orderId) {
        this.attendees = attendees;
        this.orderUuid = orderId;
    }

    public List<InLineRegistrationAttendee> getAttendees() {
        return attendees;
    }

    public String getOrderUuid() {
        return orderUuid;
    }
}
