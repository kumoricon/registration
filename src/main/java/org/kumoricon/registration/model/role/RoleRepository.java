package org.kumoricon.registration.model.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    public Role findByNameIgnoreCase(String name) {
        try {
            Role result = jdbcTemplate.queryForObject(
                    "select * from roles where name=?",
                    new Object[]{name}, new RoleRepository.RoleRowMapper());
            result.setRights(getRightIdsForRole(result.getId()));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly=true)
    public Role findById(Integer id) {
        try {
            Role result = jdbcTemplate.queryForObject(
                    "select * from roles where id=?",
                    new Object[]{id}, new RoleRepository.RoleRowMapper());
            result.setRights(getRightIdsForRole(result.getId()));
            return result;
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
    public Integer save(Role role) {
        if (role.getId() == null) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName("roles").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", role.getName());
            Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
            return (key).intValue();


        } else {
            jdbcTemplate.update("UPDATE roles SET name = ? WHERE id = ?",
                    role.getName(), role.getId());
            return role.getId();
        }
    }

    private Set<Integer> getRightIdsForRole(Integer roleId) {
        try {
            List<Integer> data = jdbcTemplate.queryForList(
                    "select roles_rights.rights_id from roles_rights where role_id = ?",
                    new Object[]{roleId}, Integer.class);
            return new HashSet<>(data);
        } catch (EmptyResultDataAccessException e) {
            return new HashSet<>();
        }
    }

    class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getInt("id"));
            role.setName(rs.getString("name"));
            return role;
        }
    }
}