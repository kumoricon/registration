package org.kumoricon.registration.model.role;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class RoleRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RoleRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Role findByNameIgnoreCase(String name) {
        try {
            Role result = jdbcTemplate.queryForObject(
                    "select * from roles where name=:name",
                    Map.of("name", name), new RoleRowMapper());
            result.setRights(getRightIdsForRole(result.getId()));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Role findById(Integer id) {
        try {
            Role result = jdbcTemplate.queryForObject("select * from roles where id=:id",
                    Map.of("id", id), new RoleRowMapper());
            result.setRights(getRightIdsForRole(result.getId()));
            return result;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return jdbcTemplate.query("select * from roles",
                new RoleRowMapper());
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from roles";
        return jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
    }

    @Transactional
    public Integer save(Role role) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(role);
        if (role.getId() == null) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate());
            jdbcInsert.withTableName("roles").usingGeneratedKeyColumns("id");
            Number key = jdbcInsert.executeAndReturnKey(params);
            return (key).intValue();
        } else {
            jdbcTemplate.update("UPDATE roles SET name = :name WHERE id = :id", params);
            return role.getId();
        }
    }

    private Set<Integer> getRightIdsForRole(Integer roleId) {
        try {
            List<Integer> data = jdbcTemplate.queryForList(
                    "select roles_rights.rights_id from roles_rights where role_id = :roleId",
                    Map.of("roleId", roleId), Integer.class);
            return new HashSet<>(data);
        } catch (EmptyResultDataAccessException e) {
            return new HashSet<>();
        }
    }

    static class RoleRowMapper implements RowMapper<Role> {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            Role role = new Role();
            role.setId(rs.getInt("id"));
            role.setName(rs.getString("name"));
            return role;
        }
    }
}