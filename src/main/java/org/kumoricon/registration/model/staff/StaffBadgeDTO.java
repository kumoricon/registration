package org.kumoricon.registration.model.staff;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffBadgeDTO {
    private final String firstName;
    private final String lastName;
    private final String fanName;
    private final String preferredPronoun;
    private final String[] positions;
    private final String department;
    private final String departmentBackgroundColor;
    private final Boolean hideDepartment;
    private final String ageRange;
    private final String ageBackgroundColor;
    private final Image badgeImage;          // User supplied image or generic mascot image
    private final Image ageImage;             // Color coded seal/chop image
    private final String badgeNumber;

    private StaffBadgeDTO(Builder builder) {
        firstName = builder.firstName;
        lastName = builder.lastName;
        preferredPronoun = builder.preferredPronoun;
        fanName = builder.fanName;
        positions = builder.positions.toArray(new String[0]);
        department = builder.department;
        departmentBackgroundColor = builder.departmentBackgroundColor;
        hideDepartment = builder.hideDepartment;
        ageRange = builder.ageRange;
        ageBackgroundColor = builder.ageBackgroundColor;
        ageImage = builder.ageImage;
        badgeImage = builder.badgeImage;
        badgeNumber = builder.badgeNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPreferredPronoun() { return preferredPronoun; }

    public String getFanName() { return fanName; }

    public String[] getPositions() {
        return positions;
    }

    public String getDepartment() {
        return department;
    }

    public String getDepartmentBackgroundColor() {
        return departmentBackgroundColor;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public Image getAgeImage() { return ageImage; }

    public String getAgeBackgroundColor() {
        return ageBackgroundColor;
    }

    public Image getBadgeImage() {
        return badgeImage;
    }

    public String getBadgeNumber() { return badgeNumber; }

    @Override
    public String toString() {
        return "StaffBadgeDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", positions=" + Arrays.toString(positions) +
                '}';
    }

    public boolean isAdult() {
        return "adult".equalsIgnoreCase(ageRange);
    }

    public boolean isYouth() {
        return "youth".equalsIgnoreCase(ageRange);
    }

    public boolean isChild() {
        return "child".equalsIgnoreCase(ageRange);
    }

    public static final class Builder {
        private String firstName;
        private String lastName;
        private String preferredPronoun;
        private String fanName;
        private List<String> positions;
        private String department;
        private String departmentBackgroundColor;
        private Boolean hideDepartment;
        private String ageRange;
        private String ageBackgroundColor;
        private Image badgeImage;
        private Image ageImage;
        private String badgeNumber;

        public Builder() {
            this.positions = new ArrayList<>();
            this.hideDepartment = false;
        }

        public String nullHandler(String input) {
            if(input == null)
                return "";

            return input;
        }

        public Builder withFirstName(String val) {
            firstName = nullHandler(val);
            return this;
        }

        public Builder withLastName(String val) {
            lastName = nullHandler(val);
            return this;
        }

        public Builder withPreferredPronoun(String val) {
            preferredPronoun = nullHandler(val);
            return this;
        }

        public Builder withFanName(String val) {
            fanName = nullHandler(val);
            return this;
        }

        public Builder withPositions(String[] val) {
            positions = Arrays.asList(val);
            return this;
        }

        public Builder withDepartment(String val) {
            department = nullHandler(val);
            return this;
        }

        public Builder withDepartmentBackgroundColor(String val) {
            departmentBackgroundColor = nullHandler(val);
            return this;
        }

        public Builder withHideDepartment(Boolean val) {
            hideDepartment = val;
            return this;
        }

        public Builder withAgeRange(String val) {
            ageRange = nullHandler(val);
            return this;
        }

        public Builder withAgeBackgroundColor(String val) {
            ageBackgroundColor = nullHandler(val);
            return this;
        }

        public Builder withBadgeImage(Image val) {
            badgeImage = val;
            return this;
        }

        public Builder withPosition(String position) {
            positions.add(position);
            return this;
        }

        public Builder withAgeImage(Image val) {
            ageImage = val;
            return this;
        }

        public Builder withBadgeNumber(String val) {
            badgeNumber = val;
            return this;
        }

        public StaffBadgeDTO build() {
            return new StaffBadgeDTO(this);
        }

    }
}
