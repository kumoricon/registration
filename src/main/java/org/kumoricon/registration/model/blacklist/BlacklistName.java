package org.kumoricon.registration.model.blacklist;

public class BlacklistName {
    private Integer id;
    private String firstName;
    private String lastName;

    public BlacklistName() {}

    public BlacklistName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String toString() {
        if (id != null) {
            return String.format("[Blacklist %s: %s %s]", id, firstName, lastName);
        } else {
            return String.format("[Blacklist: %s %s]", firstName, lastName);
        }
    }
}
