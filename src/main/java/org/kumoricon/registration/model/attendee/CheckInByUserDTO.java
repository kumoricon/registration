package org.kumoricon.registration.model.attendee;

public class CheckInByUserDTO {
    private String name;
    private Integer count;

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
