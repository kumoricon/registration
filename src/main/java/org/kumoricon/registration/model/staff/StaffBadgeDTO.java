package org.kumoricon.registration.model.staff;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffBadgeDTO {
    private String firstName;
    private String lastName;
    private String fanName;
    private String preferredPronoun;
    private String[] positions;
    private String department;
    private String departmentBackgroundColor;
    private Boolean hideDepartment;
    private String ageRange;
    private String ageBackgroundColor;
    private Image badgeImage;          // User supplied image or generic mascot image
    private Image ageImage;             // Color coded seal/chop image

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

    public Boolean getHideDepartment() {
        return hideDepartment;
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


    @Override
    public String toString() {
        return "StaffBadgeDTO{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", positions=" + Arrays.toString(positions) +
                '}';
    }

    public boolean isAdult() {
        return "adult".equals(ageRange.toLowerCase());
    }

    public boolean isYouth() {
        return "youth".equals(ageRange.toLowerCase());
    }

    public boolean isChild() {
        return "child".equals(ageRange.toLowerCase());
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

        public Builder() {
            this.positions = new ArrayList<>();
            this.hideDepartment = false;
        }

        public Builder withFirstName(String val) {
            firstName = val;
            return this;
        }

        public Builder withLastName(String val) {
            lastName = val;
            return this;
        }

        public Builder withPreferredPronoun(String val) {
            preferredPronoun = val;
            return this;
        }

        public Builder withFanName(String val) {
            fanName = val;
            return this;
        }

        public Builder withPositions(String[] val) {
            positions = Arrays.asList(val);
            return this;
        }

        public Builder withDepartment(String val) {
            department = val;
            return this;
        }

        public Builder withDepartmentBackgroundColor(String val) {
            departmentBackgroundColor = val;
            return this;
        }

        public Builder withHideDepartment(Boolean val) {
            hideDepartment = val;
            return this;
        }

        public Builder withAgeRange(String val) {
            ageRange = val;
            return this;
        }

        public Builder withAgeBackgroundColor(String val) {
            ageBackgroundColor = val;
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

        public StaffBadgeDTO build() {
            return new StaffBadgeDTO(this);
        }

    }
}
