package org.kumoricon.registration.model.role;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final RightRepository rightRepository;

    public RoleService(RoleRepository roleRepository, RightRepository rightRepository) {
        this.roleRepository = roleRepository;
        this.rightRepository = rightRepository;
    }

    public List<RoleDTO> findAll() {
        List<RoleDTO> output = new ArrayList<>();
        List<Role> roles = roleRepository.findAll();
        Map<Integer, Set<Integer>> rightsMap = rightRepository.findAllRightsByRoleId();

        for (Role role: roles) {
            RoleDTO r = new RoleDTO(role.getId(), role.getName(), rightsMap.get(role.getId()));
            output.add(r);
        }
        return output;
    }

    public List<Right> findAllRights() {
        return rightRepository.findAll();
    }
}
