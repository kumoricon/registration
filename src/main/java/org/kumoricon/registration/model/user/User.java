package org.kumoricon.registration.model.user;

import org.kumoricon.registration.model.role.Right;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;


public class User implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean credentialsNonExpired;              // On login, prompt to reset password
    private Boolean accountNonLocked;
    private Integer roleId;
    private Integer lastBadgeNumberCreated;
    private Set<Right> rights;

    /**
     * Creating a new user? Use UserFactory instead of creating the user object directly
     */
    public User() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getRoleId() { return roleId; }

    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public Set<Right> getRights() { return rights; }
    public void setRights(Set<Right> rights) { this.rights = rights; }

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
        return rights;
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

    public boolean hasRight(String right) {
        if (rights != null) {
            return rights.contains(right);
        } else {
            return false;
        }
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
