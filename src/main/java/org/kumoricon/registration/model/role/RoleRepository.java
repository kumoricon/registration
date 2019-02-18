package org.kumoricon.registration.model.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByNameStartsWithIgnoreCase(String lastName);
    Role findByNameIgnoreCase(String roleName);

    @Query(value = "SELECT roles.name as Role, rights.name as Rights FROM roles JOIN roles_rights ON roles.id = roles_rights.role_id JOIN rights ON rights.id = roles_rights.rights_id ORDER BY Role", nativeQuery = true)
    List<Object[]> findAllRoles();
}