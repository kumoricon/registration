package org.kumoricon.registration.model.staff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Position {
    private Integer id;
    private String term;
    private String team;
    private String title;
    private String level;
    private Boolean isElected;
    private String department;

    private static final Map<String, List<String>> DEPARTMENT_TEAMS = Map.of(
            "Membership", List.of("Registration", "Staff Registration", "Registration Hall", "Registration Software",
                    "Specialty Registration", "Attendee Registration", "Membership"),
            "Infrastructure", List.of("IT", "KumoriMarket", "Hotels", "Infrastructure", "Facilities", "Infrastructure Office", "Convention Center"),
            "Programming", List.of("Panels", "Fan Art", "Auditorium Tech", "Cosplay Contest",
                    "Online Content", "Tabletop Library", "Lip Sync", "Special Events", "Karaoke", "Chibi Room",
                    "Console Gaming", "Cabinet and Rhythm Gaming", "Manga Library", "Maid Cafe", "Cosplay Chess",
                    "Video Gaming Tournaments", "Crafts", "Cultural Gaming", "Main Events", "AMV Contest", "Ballroom",
                    "RPG", "Idol Festival", "LARP", "PC Gaming", "Live Events", "Tabletop Gaming", "Escape Room",
                    "Video Gaming", "Fan Fiction", "Play and Win", "Gunpla Lounge", "Pokémon Gaming Assistant Coordinator",
                    "Programming", "Programming Office", "TCG", "Lighting Equipment", "Video Equipment", "Programming Booth",
                    "Pokémon Gaming", "Umbrella Lounge", "Cosplay Cabaret", "Viewing Rooms", "Fashion", "Sound Equipment"),
            "Publicity", List.of("Hall Cosplay", "Info Booth", "Software", "Merchandise", "Press", "Social Media",
                    "Website", "Marketing", "Newsletter", "Multimedia", "Graphics", "Publicity", "Moderation", "Lore", "Groupware", "Branding", "Signs", "Copy Editing", "Advertising"),
            "Relations", List.of("Relations", "Autographs", "Relations Logistics", "Hospitality", "Industry and Sponsorship",
                    "Guests"),
            "Operations", List.of("Operations", "Hotel Attendee Services", "Attendee Services", "Operations Office", "Accessibility",
                    "Cosplay Repair", "Staff Station", "Public Discord", "Floor Support", "Administration", "Food and Beverage", "Incidents Response",
                    "Amenities and Learning", "Knowledge and Documentation", "Peace-Bonding", "Crowd Control", "Risk Management", "Patrol"),
            "Treasury", List.of("Treasury", "Supply and Logistics"),
            "Secretarial", List.of("Secretary"),
            "Chair", List.of("Chair", "EDI", "Staff Retention", "Internal Support", "Outreach", "Staff Events",
                    "Training and Development", "Charity", "External Support", "Art Show", "Photobooth", "Nonprofit Grants",
                    "Recruitment", "Staff Relations")
    );

    public Position() {

    }

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

        throw new RuntimeException("Unknown department for team " + team);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean isElected() {
        return isElected;
    }

    public void setIsElected(Boolean elected) {
        isElected = elected;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
