package org.kumoricon.registration.staff.staffimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffYearsOfServiceImportFile {
    private final List<StaffYearsOfService> staffYearsList;

    public StaffYearsOfServiceImportFile(@JsonProperty(value = "staff_years") List<StaffYearsOfService> staffYearsList) {
        this.staffYearsList = staffYearsList;
    }

    public List<StaffYearsOfService> getStaffYearsList() {
        return staffYearsList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class StaffYearsOfService {
        private final String uuid;
        private final String firstName;
        private final String lastName;
        private final Integer yearsOfService;

        @JsonCreator
        public StaffYearsOfService(
                @JsonProperty(value = "staff_id", required = true) String uuid,
                @JsonProperty(value = "first_name") String firstName,
                @JsonProperty(value = "last_name") String lastName,
                @JsonProperty(value = "number_of_years") Integer yearsOfService) {
            this.uuid = uuid;
            this.firstName = firstName;
            this.lastName = lastName;
            this.yearsOfService = yearsOfService;
        }

        public String getUuid() {
            return uuid;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public Integer getYearsOfService() {
            return yearsOfService;
        }
    }
}

