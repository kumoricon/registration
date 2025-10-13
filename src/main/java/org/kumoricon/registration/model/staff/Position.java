package org.kumoricon.registration.model.staff;

public class Position {
    private Integer id;
    private String term;
    private String team;
    private String title;
    private String level;
    private Boolean isElected;
    private String department;

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

    @Override
    public String toString() {
        return String.format("%s/%s %s", department, team, title);
    }
}
