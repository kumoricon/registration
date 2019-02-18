package org.kumoricon.registration.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StaffRepository extends JpaRepository<User, Integer> {
    List<User> findByLastNameStartsWithIgnoreCase(String lastName);
    User findOneByUsernameIgnoreCase(String username);
    User findOneById(Integer id);
    User findOneByUuid(String uuid);

    @Query(value = "SELECT first_name, last_name, username, id, (SELECT roles.name FROM roles WHERE roles.id = role_id) AS Role FROM users", nativeQuery = true)
    List<User> findAll();
}
