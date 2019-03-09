package org.kumoricon.registration.model.role;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Role implements Serializable {
    private Integer id;
    private String name;

    private Set<Integer> rights;

    public Role() {
        this.rights = new HashSet<>();
    }

    public Role(String name) {
        this();
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void addRight(Right right) { rights.add(right.getId()); }
    public void addRights(List<Right> rights) {
        for (Right right : rights) {
            this.rights.add(right.getId());
        }
    }

    /**
     * Returns true if this role has the given right
     * @param id ID of Right
     * @return boolean
     */
    public boolean hasRight(Integer id) {
        if (name == null) { return false; }
        return rights.contains(id);
    }

    public String toString() {
        if (id != null) {
            return String.format("[Role %s: %s]", id, name);
        } else {
            return String.format("[Role: %s]", name);
        }
    }

    public void setRights(Set<Integer> rightIdsForRole) {
        this.rights = rightIdsForRole;
    }

    public Set<Integer> getRightIds() {
        return this.rights;
    }
}
