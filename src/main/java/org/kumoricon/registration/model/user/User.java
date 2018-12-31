package org.kumoricon.registration.model.user;



import org.kumoricon.registration.model.Record;
import org.kumoricon.registration.model.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;


@Entity
@Table(name = "users")
public class User extends Record implements UserDetails {
    @NotNull
    @Column(length = 200, unique = true)
    private String username;
    @NotNull
    private String password;
    private String firstName;
    private String lastName;
    @NotNull
    private Boolean enabled;

    @NotNull
    private Boolean accountNonExpired;
    @NotNull
    private Boolean credentialsNonExpired;              // On login, prompt to reset password
    @NotNull
    private Boolean accountNonLocked;
    @ManyToOne(cascade= CascadeType.MERGE)
    private Role role;

    @NotNull
    private Integer lastBadgeNumberCreated;

    /**
     * Creating a new user? Use UserFactory instead of creating the user object directly
     */
    public User() {}

    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        if (username != null) { this.username = username.toLowerCase(); }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return null;
        }
        return role.getRights();
    }

    public String getPassword() { return password; }
    public void setPassword(String newPassword) {
        if (newPassword == null || newPassword.trim().equals("")) {
            throw new RuntimeException("Password cannot be blank");
        }
        this.password = newPassword;
    }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }


    /**
     * Get a string that can be used to prefix the badge number. This has to be unique to each user, and should
     * generally be 2-3 characters. Other things to watch out for: inadvertant bad words. For example, giving
     * someone a badge number "SEX001234" would be bad (if funny later).
     *
     * This used to be based on the user's initials, but there were a surprising number of collisions. So changed
     * it to the database ID of the user in hex, plus 42 (So it doesn't start at "00").
     *
     * @return String
     */
    public String getBadgePrefix() {
        if (id != null) {
            return Integer.toHexString(id + 42).toUpperCase();
        }
        return "00";
    }

    public Integer getLastBadgeNumberCreated() { return lastBadgeNumberCreated; }
    public void setLastBadgeNumberCreated(Integer lastBadgeNumberCreated) { this.lastBadgeNumberCreated = lastBadgeNumberCreated; }
    public Integer getNextBadgeNumber() {
        if (lastBadgeNumberCreated == null) { lastBadgeNumberCreated = 0; }
        lastBadgeNumberCreated += 1;
        return lastBadgeNumberCreated;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String toString() {
        if (id != null) {
            return String.format("[User %s: %s]", id, username);
        } else {
            return String.format("[User: %s]", username);
        }
    }


    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean hasRight(String right) {
        return role != null && role.hasRight(right);
    }
}
