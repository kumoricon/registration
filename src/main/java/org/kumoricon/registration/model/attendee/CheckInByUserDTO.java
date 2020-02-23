package org.kumoricon.registration.model.attendee;

public class CheckInByUserDTO {
    private final String name;
    private final Integer count;

    public CheckInByUserDTO(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }
}
