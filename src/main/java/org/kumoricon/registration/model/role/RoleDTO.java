package org.kumoricon.registration.model.role;

import java.util.Set;

public class RoleDTO {
    private Integer id;
    private String name;
    private Set<Integer> rightIds;

    public RoleDTO(Integer id, String name, Set<Integer> rightIds) {
        this.id = id;
        this.name = name;
        this.rightIds = rightIds;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasRightId(Integer id) {
        return rightIds.contains(id);
    }
}
