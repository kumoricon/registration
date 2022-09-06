package org.kumoricon.registration.guest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        public static final class Person {
        private final String id;
        private final String namePreferredFirst;
        private final String namePreferredLast;
        private final Boolean nameOnIdSame;
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
        private final String vipLevel;
        private final String membershipType;
        private final Integer amountPaidCents;

            @JsonCreator
            public Person(
                    @JsonProperty(value = "id", required = true) String id,
                    @JsonProperty(value = "namePreferredFirst") String namePreferredFirst,
                    @JsonProperty(value = "namePreferredLast") String namePreferredLast,
                    @JsonProperty(value = "nameOnIdSame") Boolean nameOnIdSame,
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
                this.nameOnIdSame = nameOnIdSame;
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

            public String getOrderId() {
                // id will be something like: n1xz94s5l3yzt9wscmhin1vm3ks7vnn0-0 where orderId is the first part
                if (this.id.contains("-")) {
                    return this.id.split("-")[0];
                } else {
                    throw new RuntimeException("Unknown ID format in %s (%s %s)".formatted(
                            this.id, this.namePreferredFirst, this.namePreferredLast));
                }
            }

            @Override
            public String toString() {
                return String.format("[Person %s: %s %s]", id, namePreferredFirst, namePreferredLast);
            }

        public String id() {
            return id;
        }

        public String namePreferredFirst() {
            return namePreferredFirst;
        }

        public String namePreferredLast() {
            return namePreferredLast;
        }

        public Boolean nameOnIdSame() {
            return nameOnIdSame;
        }

        public String nameOnIdFirst() {
            return nameOnIdFirst;
        }

        public String nameOnIdLast() {
            return nameOnIdLast;
        }

        public String namePrivacyFirst() {
            return namePrivacyFirst;
        }

        public String namePrivacyLast() {
            return namePrivacyLast;
        }

        public String preferredPronoun() {
            return preferredPronoun;
        }

        public String fanName() {
            return fanName;
        }

        public String tShirtSize() {
            return tShirtSize;
        }

        public String badgeImpactingLastModified() {
            return badgeImpactingLastModified;
        }

        public String birthdate() {
            return birthdate;
        }

        public String ageCategoryConCurrentTerm() {
            return ageCategoryConCurrentTerm;
        }

        public Boolean hasBadgeImage() {
            return hasBadgeImage;
        }

        public String badgeImageFileType() {
            return badgeImageFileType;
        }

        public Long detailsVersion() {
            return detailsVersion;
        }

        public Boolean isCanceled() {
            if (isCanceled == null) return false;
            return isCanceled;
        }

        public String country() {
            return country;
        }

        public String email() {
            return email;
        }

        public String phone() {
            return phone;
        }

        public String postal() {
            return postal;
        }

        public String emergencyName() {
            return emergencyName;
        }

        public String emergencyPhone() {
            return emergencyPhone;
        }

        public Boolean parentContactSeparate() {
            return parentContactSeparate;
        }

        public String parentName() {
            return parentName;
        }

        public String parentPhone() {
            return parentPhone;
        }

        public String hearAbout() {
            return hearAbout;
        }

        public String notes() {
            return notes;
        }

        public String vipLevel() {
            return vipLevel;
        }

        public String membershipType() {
            return membershipType;
        }

        public Integer amountPaidCents() {
            return amountPaidCents;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Person) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.namePreferredFirst, that.namePreferredFirst) &&
                    Objects.equals(this.namePreferredLast, that.namePreferredLast) &&
                    Objects.equals(this.nameOnIdSame, that.nameOnIdSame) &&
                    Objects.equals(this.nameOnIdFirst, that.nameOnIdFirst) &&
                    Objects.equals(this.nameOnIdLast, that.nameOnIdLast) &&
                    Objects.equals(this.namePrivacyFirst, that.namePrivacyFirst) &&
                    Objects.equals(this.namePrivacyLast, that.namePrivacyLast) &&
                    Objects.equals(this.preferredPronoun, that.preferredPronoun) &&
                    Objects.equals(this.fanName, that.fanName) &&
                    Objects.equals(this.tShirtSize, that.tShirtSize) &&
                    Objects.equals(this.badgeImpactingLastModified, that.badgeImpactingLastModified) &&
                    Objects.equals(this.birthdate, that.birthdate) &&
                    Objects.equals(this.ageCategoryConCurrentTerm, that.ageCategoryConCurrentTerm) &&
                    Objects.equals(this.hasBadgeImage, that.hasBadgeImage) &&
                    Objects.equals(this.badgeImageFileType, that.badgeImageFileType) &&
                    Objects.equals(this.detailsVersion, that.detailsVersion) &&
                    Objects.equals(this.isCanceled, that.isCanceled) &&
                    Objects.equals(this.country, that.country) &&
                    Objects.equals(this.email, that.email) &&
                    Objects.equals(this.phone, that.phone) &&
                    Objects.equals(this.postal, that.postal) &&
                    Objects.equals(this.emergencyName, that.emergencyName) &&
                    Objects.equals(this.emergencyPhone, that.emergencyPhone) &&
                    Objects.equals(this.parentContactSeparate, that.parentContactSeparate) &&
                    Objects.equals(this.parentName, that.parentName) &&
                    Objects.equals(this.parentPhone, that.parentPhone) &&
                    Objects.equals(this.hearAbout, that.hearAbout) &&
                    Objects.equals(this.notes, that.notes) &&
                    Objects.equals(this.vipLevel, that.vipLevel) &&
                    Objects.equals(this.membershipType, that.membershipType) &&
                    Objects.equals(this.amountPaidCents, that.amountPaidCents);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, namePreferredFirst, namePreferredLast, nameOnIdSame, nameOnIdFirst, nameOnIdLast, namePrivacyFirst, namePrivacyLast, preferredPronoun, fanName, tShirtSize, badgeImpactingLastModified, birthdate, ageCategoryConCurrentTerm, hasBadgeImage, badgeImageFileType, detailsVersion, isCanceled, country, email, phone, postal, emergencyName, emergencyPhone, parentContactSeparate, parentName, parentPhone, hearAbout, notes, vipLevel, membershipType, amountPaidCents);
        }

        }
}
