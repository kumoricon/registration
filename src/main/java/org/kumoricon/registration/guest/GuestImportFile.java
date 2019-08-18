package org.kumoricon.registration.guest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuestImportFile {
    private List<Action> actions;
    private List<Person> persons;

    @JsonCreator
    public GuestImportFile(
            @JsonProperty(value = "actions") List<Action> actions,
            @JsonProperty(value = "persons") List<Person> persons) {
        this.actions = actions == null ? new ArrayList<>() : actions;
        this.persons = persons == null ? new ArrayList<>() : persons;
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<Person> getPersons() {
        return persons;
    }


    static class Action {
        private Long actionsVersion;
        private List<String> deleted;

        @JsonCreator
        public Action(
                @JsonProperty(value = "actionsVersion", required = true) Long actionsVersion,
                @JsonProperty(value = "deleted", required = true) List<String> deleted) {
            this.actionsVersion = actionsVersion;
            this.deleted = deleted;
        }

        public long getActionsVersion() {
            return actionsVersion;
        }

        public List<String> getDeleted() {
            return deleted;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Person {
        private String id;
        private String namePreferredFirst;
        private String namePreferredLast;
        private String nameOnIdFirst;
        private String nameOnIdLast;
        private String fanName;
        private String tShirtSize;
        private String badgeImpactingLastModified;
        private String birthdate;
        private String ageCategoryConCurrentTerm;
        private Boolean hasBadgeImage;
        private String badgeImageFileType;
        private Long detailsVersion;

        @JsonCreator
        public Person(
                @JsonProperty(value = "id", required = true) String id,
                @JsonProperty(value = "namePreferredFirst") String namePreferredFirst,
                @JsonProperty(value = "namePreferredLast") String namePreferredLast,
                @JsonProperty(value = "nameOnIdFirst") String nameOnIdFirst,
                @JsonProperty(value = "nameOnIdLast") String nameOnIdLast,
                @JsonProperty(value = "fanName") String fanName,
                @JsonProperty(value = "tShirtSize") String tShirtSize,
                @JsonProperty(value = "badgeImpactingLastModified") String badgeImpactingLastModified,
                @JsonProperty(value = "birthdate") String birthdate,
                @JsonProperty(value = "ageCategoryConCurrentTerm") String ageCategoryConCurrentTerm,
                @JsonProperty(value = "hasBadgeImage") Boolean hasBadgeImage,
                @JsonProperty(value = "badgeImageFileType") String badgeImageFileType,
                @JsonProperty(value = "detailsVersion") Long detailsVersion) {
            this.id = id;
            this.namePreferredFirst = namePreferredFirst;
            this.namePreferredLast = namePreferredLast;
            this.nameOnIdFirst = nameOnIdFirst;
            this.nameOnIdLast = nameOnIdLast;
            this.tShirtSize = tShirtSize;
            this.badgeImpactingLastModified = badgeImpactingLastModified;
            this.birthdate = birthdate;
            this.ageCategoryConCurrentTerm = ageCategoryConCurrentTerm;
            this.hasBadgeImage = hasBadgeImage;
            this.badgeImageFileType = badgeImageFileType;
            this.detailsVersion = detailsVersion;
        }

        public String getId() {
            return id;
        }

        public String getNamePreferredFirst() {
            return namePreferredFirst;
        }

        public String getNamePreferredLast() {
            return namePreferredLast;
        }

        public String getNameOnIdFirst() {
            return nameOnIdFirst;
        }

        public String getNameOnIdLast() {
            return nameOnIdLast;
        }

        public String getFanName() { return fanName; }

        public String gettShirtSize() {
            return tShirtSize;
        }

        public String getBadgeImpactingLastModified() {
            return badgeImpactingLastModified;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public String getAgeCategoryConCurrentTerm() {
            return ageCategoryConCurrentTerm;
        }

        public Boolean getHasBadgeImage() {
            return hasBadgeImage;
        }

        public String getBadgeImageFileType() {
            return badgeImageFileType;
        }

        public Long getDetailsVersion() {
            return detailsVersion;
        }
    }
}
