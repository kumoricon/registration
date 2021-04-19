package org.kumoricon.registration.model.role;

import java.util.Set;

public record RoleDTO (Integer id, String name, Set<Integer> rightIds) {
    public boolean hasRightId(Integer id) {
        return rightIds.contains(id);
    }
}
