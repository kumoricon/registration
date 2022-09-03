package org.kumoricon.registration.guest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
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
        private final String namePrivacyFirst;
        private final String namePrivacyLast;
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
        private final String country;
        private final String email;
        private final String phone;
        private final String postal;
        private final String emergencyName;
        private final String emergencyPhone;
        private final Boolean parentContactSeparate;
        private final String parentName;
        private final String parentPhone;
        private final String hearAbout;
        private final String notes;
        private final String membershipType;
        private final String vipLevel;
        private final Integer amountPaidCents;

        @JsonCreator
        public Person(
                @JsonProperty(value = "id", required = true) String id,
                @JsonProperty(value = "namePreferredFirst") String namePreferredFirst,
                @JsonProperty(value = "namePreferredLast") String namePreferredLast,
                @JsonProperty(value = "nameOnIdFirst") String nameOnIdFirst,
                @JsonProperty(value = "nameOnIdLast") String nameOnIdLast,
                @JsonProperty(value = "nomePrivacyFirst") String namePrivacyFirst,
                @JsonProperty(value = "namePrivacyLast") String namePrivacyLast,
                @JsonProperty(value = "pronouns") String preferredPronoun,
                @JsonProperty(value = "fanName") String fanName,
                @JsonProperty(value = "tShirtSize") String tShirtSize,
                @JsonProperty(value = "badgeImpactingLastModified") String badgeImpactingLastModified,
                @JsonProperty(value = "birthdate") String birthdate,
                @JsonProperty(value = "ageCategoryConCurrentTerm") String ageCategoryConCurrentTerm,
                @JsonProperty(value = "hasBadgeImage") Boolean hasBadgeImage,
                @JsonProperty(value = "badgeImageFileType") String badgeImageFileType,
                @JsonProperty(value = "detailsVersion") Long detailsVersion,
                @JsonProperty(value = "isCanceled", defaultValue = "false") Boolean isCanceled,
                @JsonProperty(value = "country") String country,
                @JsonProperty(value = "email") String email,
                @JsonProperty(value = "phone") String phone,
                @JsonProperty(value = "postal") String postal,
                @JsonProperty(value = "emergencyName") String emergencyName,
                @JsonProperty(value = "emergencyPhone") String emergencyPhone,
                @JsonProperty(value = "parentContactSeparate") Boolean parentContactSeparate,
                @JsonProperty(value = "parentName") String parentName,
                @JsonProperty(value = "parentPhone") String parentPhone,
                @JsonProperty(value = "hearAbout") String hearAbout,
                @JsonProperty(value = "notes") String notes,
                @JsonProperty(value = "vipLevel") String vipLevel,
                @JsonProperty(value = "membershipType") String membershipType,
                @JsonProperty(value = "amountPaidCents") Integer amountPaidCents) {
            this.id = id;
            this.namePreferredFirst = namePreferredFirst;
            this.namePreferredLast = namePreferredLast;
            this.nameOnIdFirst = nameOnIdFirst;
            this.nameOnIdLast = nameOnIdLast;
            this.namePrivacyFirst = namePrivacyFirst;
            this.namePrivacyLast = namePrivacyLast;
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
            this.country = country;
            this.email = email;
            this.phone = phone;
            this.postal = postal;
            this.emergencyName = emergencyName;
            this.emergencyPhone = emergencyPhone;
            this.parentContactSeparate = parentContactSeparate;
            this.parentName = parentName;
            this.parentPhone = parentPhone;
            this.hearAbout = hearAbout;
            this.notes = notes;
            this.membershipType = membershipType;
            this.vipLevel = vipLevel;
            this.amountPaidCents = amountPaidCents;
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

        public String getNamePrivacyFirst() {
            return namePrivacyFirst;
        }

        public String getNamePrivacyLast() {
            return namePrivacyLast;
        }

        public String getCountry() {
            return country;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getPostal() {
            return postal;
        }

        public String getEmergencyName() {
            return emergencyName;
        }

        public String getEmergencyPhone() {
            return emergencyPhone;
        }

        public Boolean getParentContactSeparate() {
            return parentContactSeparate;
        }

        public String getParentName() {
            return parentName;
        }

        public String getParentPhone() {
            return parentPhone;
        }

        public String getHearAbout() {
            return hearAbout;
        }

        public String getNotes() {
            return notes;
        }

        public Integer getAmountPaidCents() {
            return amountPaidCents;
        }

        public String getMembershipType() {
            return membershipType;
        }

        public String getVipLevel() {
            return vipLevel;
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
