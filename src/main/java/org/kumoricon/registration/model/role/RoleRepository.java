package org.kumoricon.registration.model.role;

import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


//    @Query(value = "SELECT roles.name as Role, rights.name as Rights FROM roles JOIN roles_rights ON roles.id = roles_rights.role_id JOIN rights ON rights.id = roles_rights.rights_id ORDER BY Role, Rights",
//            nativeQuery = true)
    public List<Object> findAllRoles() {
        return new ArrayList<>();
    }

    @Transactional(readOnly=true)
    public Role findByNameIgnoreCase(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from roles where name=?",
                    new Object[]{name}, new RoleRepository.RoleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly=true)
    public Role findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from roles where id=?",
                    new Object[]{id}, new RoleRepository.RoleRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional(readOnly=true)
    public List<Role> findAll() {
        return jdbcTemplate.query("select * from roles",
                new RoleRowMapper());
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from roles";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Transactional
    public void save(Role role) {
        if (role.getId() == null) {
            jdbcTemplate.update("INSERT INTO roles " +
                            "(name) " +
                            "VALUES(?)",
                    role.getName());
        } else {
            jdbcTemplate.update("UPDATE roles SET name = ? WHERE id = ?",
                    role.getName(), role.getId());
        }
    }

    class RoleRowMapper implements RowMapper<Role>
    {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getInt("id"));
            role.setName(rs.getString("name"));
            return role;
        }
    }

}