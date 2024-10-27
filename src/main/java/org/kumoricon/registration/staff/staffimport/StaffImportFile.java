package org.kumoricon.registration.staff.staffimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffImportFile {
    private final List<Action> actions;
    private final List<Person> persons;

    @JsonCreator
    public StaffImportFile (
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
        private final String namePrivacyFirst;
        private final String namePrivacyLast;
        private final String nameOnIdFirst;
        private final String nameOnIdLast;
        private final String phoneNumber;
        private final String preferredPronoun;
        private final String tShirtSize;
        private final String badgeImpactingLastModified;
        private final List<Position> positions;
        private final String birthdate;
        private final String ageCategoryConCurrentTerm;
        private final Boolean hasBadgeImage;
        private final String badgeImageFileType;
        private final Long detailsVersion;

        @JsonCreator
        public Person(
                @JsonProperty(value = "id", required = true) String id,
                @JsonProperty(value = "namePreferredFirst") String namePreferredFirst,
                @JsonProperty(value = "namePreferredLast") String namePreferredLast,
                @JsonProperty(value = "namePrivacyFirst") String namePrivacyFirst,
                @JsonProperty(value = "namePrivacyLast") String namePrivacyLast,
                @JsonProperty(value = "nameOnIdFirst") String nameOnIdFirst,
                @JsonProperty(value = "nameOnIdLast") String nameOnIdLast,
                @JsonProperty(value = "phone") String phoneNumber,
                @JsonProperty(value = "pronouns") String preferredPronoun,
                @JsonProperty(value = "tShirtSize") String tShirtSize,
                @JsonProperty(value = "badgeImpactingLastModified") String badgeImpactingLastModified,
                @JsonProperty(value = "positions") List<Position> positions,
                @JsonProperty(value = "birthdate") String birthdate,
                @JsonProperty(value = "ageCategoryConCurrentTerm") String ageCategoryConCurrentTerm,
                @JsonProperty(value = "hasBadgeImage") Boolean hasBadgeImage,
                @JsonProperty(value = "badgeImageFileType") String badgeImageFileType,
                @JsonProperty(value = "detailsVersion") Long detailsVersion) {
            this.id = id;
            this.namePreferredFirst = namePreferredFirst;
            this.namePreferredLast = namePreferredLast;
            this.namePrivacyFirst = namePrivacyFirst;
            this.namePrivacyLast = namePrivacyLast;
            this.nameOnIdFirst = nameOnIdFirst;
            this.nameOnIdLast = nameOnIdLast;
            this.phoneNumber = phoneNumber;
            this.preferredPronoun = preferredPronoun;
            this.tShirtSize = tShirtSize;
            this.badgeImpactingLastModified = badgeImpactingLastModified;
            this.positions = positions;
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

        public String getNamePrivacyFirst() { return namePrivacyFirst; }

        public String getNamePrivacyLast() { return namePrivacyLast; }

        public String getNameOnIdFirst() {
            return nameOnIdFirst;
        }

        public String getNameOnIdLast() {
            return nameOnIdLast;
        }

        public String getPhoneNumber() { return phoneNumber; }

        public String getPreferredPronoun() { return preferredPronoun; }

        public String gettShirtSize() {
            return tShirtSize;
        }

        public String getBadgeImpactingLastModified() {
            return badgeImpactingLastModified;
        }

        public List<Position> getPositions() {
            return positions;
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

        @Override
        public String toString() {
            return String.format("[Person %s: %s %s]", id, namePreferredFirst, namePreferredLast);
        }
    }

    static class Position {
        public final String term;
        public final String team;
        public final String title;
        public final String level;
        public final Boolean isElected;
        public final String department;

        private static final Map<String, List<String>> DEPARTMENT_TEAMS = Map.of(
                "Membership", List.of("Registration", "Staff Registration", "Registration Hall", "Registration Software",
                        "Specialty Registration", "Attendee Registration", "Membership"),
                "Infrastructure", List.of("IT", "KumoriMarket", "Hotels", "Infrastructure", "Facilities"),
                "Programming", List.of("Panels", "Fan Art", "Auditorium Tech", "Cosplay Contest",
                        "Online Content", "Tabletop Library", "Lip Sync", "Special Events", "Karaoke", "Chibi Room",
                        "Console Gaming", "Cabinet and Rhythm Gaming", "Manga Library", "Maid Cafe", "Cosplay Chess",
                        "Video Gaming Tournaments", "Crafts", "Cultural Gaming", "Main Events", "AMV Contest", "Ballroom",
                        "RPG", "Idol Festival", "LARP", "PC Gaming", "Live Events", "Tabletop Gaming", "Escape Room",
                        "Video Gaming", "Fan Fiction", "Play and Win", "Gunpla Lounge", "Pokémon Gaming Assistant Coordinator",
                        "Programming", "Programming Office", "TCG", "Lighting Equipment", "Video Equipment", "Programming Booth",
                        "Pokémon Gaming"),
                "Publicity", List.of("Hall Cosplay", "Info Booth", "Software", "Merchandise", "Press", "Social Media",
                        "Website", "Marketing", "Newsletter", "Multimedia", "Graphics", "Publicity"),
                "Relations", List.of("Relations", "Autographs", "Relations Logistics", "Hospitality", "Industry and Sponsorship",
                        "Guests"),
                "Operations", List.of("Hotel Attendee Services", "Attendee Services", "Operations Office", "Accessibility",
                        "Cosplay Repair", "Operations"),
                "Treasury", List.of("Treasury", "Supply and Logistics"),
                "Secretarial", List.of("Secretary"),
                "Chair", List.of("Chair", "EDI", "Staff Retention", "Internal Support", "Outreach", "Staff Events",
                        "Training and Development", "Charity", "External Support", "Art Show", "Photobooth", "Nonprofit Grants")
        );

        @JsonCreator
        public Position(
                @JsonProperty(value = "term", required = true) String term,
                @JsonProperty(value = "team", required = true) String team,
                @JsonProperty(value = "title", required = true) String title,
                @JsonProperty(value = "level", required = true) String level,
                @JsonProperty(value = "isElected", required = true) Boolean isElected) {
            this.term = term;
            this.team = team;
            this.title = title;
            this.level = level;
            this.isElected = isElected;
            this.department = getDepartmentFromTeam(team);
        }

        private String getDepartmentFromTeam(final String team) {
            for (final String dept : DEPARTMENT_TEAMS.keySet()) {
                if (DEPARTMENT_TEAMS.get(dept).contains(team)) {
                    return dept;
                }
            }
            System.out.println(team);

            return "Membership";
        }
    }
}
