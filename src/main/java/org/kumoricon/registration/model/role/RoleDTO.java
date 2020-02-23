package org.kumoricon.registration.model.role;

import java.util.Set;

public class RoleDTO {
    private final Integer id;
    private final String name;
    private final Set<Integer> rightIds;

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
