package org.kumoricon.registration.model.blacklist;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlacklistName that = (BlacklistName) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(firstName, that.firstName)) return false;
        return Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        return result;
    }

    public boolean isBlank() {
        return (firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank());
    }
}
