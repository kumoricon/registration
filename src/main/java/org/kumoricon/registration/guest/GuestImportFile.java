package org.kumoricon.registration.guest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GuestImportFile {
    private final List<Action> actions;
    private final List<Person> persons;

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
        private final Long actionsVersion;
        private final List<String> deleted;

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
        private final String id;
        private final String namePreferredFirst;
        private final String namePreferredLast;
        private final String nameOnIdFirst;
        private final String nameOnIdLast;
        private final String preferredPronoun;
        private final String fanName;
        private final String tShirtSize;
        private final String badgeImpactingLastModified;
        private final String birthdate;
        private final String ageCategoryConCurrentTerm;
        private final Boolean hasBadgeImage;
        private final String badgeImageFileType;
        private final Long detailsVersion;
        private final Boolean isCanceled;

        @JsonCreator
        public Person(
                @JsonProperty(value = "id", required = true) String id,
                @JsonProperty(value = "namePreferredFirst") String namePreferredFirst,
                @JsonProperty(value = "namePreferredLast") String namePreferredLast,
                @JsonProperty(value = "nameOnIdFirst") String nameOnIdFirst,
                @JsonProperty(value = "nameOnIdLast") String nameOnIdLast,
                @JsonProperty(value = "pronouns") String preferredPronoun,
                @JsonProperty(value = "fanName") String fanName,
                @JsonProperty(value = "tShirtSize") String tShirtSize,
                @JsonProperty(value = "badgeImpactingLastModified") String badgeImpactingLastModified,
                @JsonProperty(value = "birthdate") String birthdate,
                @JsonProperty(value = "ageCategoryConCurrentTerm") String ageCategoryConCurrentTerm,
                @JsonProperty(value = "hasBadgeImage") Boolean hasBadgeImage,
                @JsonProperty(value = "badgeImageFileType") String badgeImageFileType,
                @JsonProperty(value = "detailsVersion") Long detailsVersion,
                @JsonProperty(value = "isCanceled", defaultValue = "false") Boolean isCanceled) {
            this.id = id;
            this.namePreferredFirst = namePreferredFirst;
            this.namePreferredLast = namePreferredLast;
            this.nameOnIdFirst = nameOnIdFirst;
            this.nameOnIdLast = nameOnIdLast;
            this.preferredPronoun = preferredPronoun;
            this.fanName = fanName;
            this.tShirtSize = tShirtSize;
            this.badgeImpactingLastModified = badgeImpactingLastModified;
            this.birthdate = birthdate;
            this.ageCategoryConCurrentTerm = ageCategoryConCurrentTerm;
            this.hasBadgeImage = hasBadgeImage;
            this.badgeImageFileType = badgeImageFileType;
            this.detailsVersion = detailsVersion;
            this.isCanceled = isCanceled;
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

        public String getPreferredPronoun() { return preferredPronoun; }

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

        public Boolean isCanceled() {
            if (isCanceled == null) return false;
            return isCanceled;
        }

        @Override
        public String toString() {
            return String.format("[Person %s: %s %s]", id, namePreferredFirst, namePreferredLast);
        }
    }
}
