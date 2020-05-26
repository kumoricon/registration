package org.kumoricon.registration.model.role;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class RightRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RightRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Right findByNameIgnoreCase(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from rights where name=:name",
                    Map.of("name", name), new RightRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Map<Integer, Set<Integer>> findAllRightsByRoleId() {
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "select roles_rights.* FROM roles_rights", Map.of());

            Map<Integer, Set<Integer>> data = new HashMap<>();
            for (Map<String, Object> row : rows) {
                Integer roleId = (Integer)row.get("role_id");
                if (!data.containsKey(roleId)) {
                    data.put(roleId, new HashSet<>());
                }
                data.get(roleId).add((Integer)row.get("rights_id"));
            }
            return data;

        } catch (EmptyResultDataAccessException e) {
            return new HashMap<>();
        }
    }


    @Transactional(readOnly = true)
    public Set<Right> findAllRightsByUserId(Integer id) {
        try {
            List<Right> data = jdbcTemplate.query(
                    "select * from rights where rights.id in (select roles_rights.rights_id from roles_rights join users on roles_rights.role_id = users.role_id where users.id = :id ); ",
                    Map.of("id", id), new RightRowMapper());

            return new HashSet<>(data);
        } catch (EmptyResultDataAccessException e) {
            return new HashSet<>();
        }
    }

    @Transactional
    public void save(Right right) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(right);
        if (right.getId() == null) {
            jdbcTemplate.update("INSERT INTO rights " +
                            "(name, description) " +
                            "VALUES(:name, :description)",
                    params);
        } else {
            jdbcTemplate.update("UPDATE rights SET name = :name, description = :description WHERE id = :id",
                    params);
        }
    }

    @Transactional(readOnly = true)
    public List<Right> findAll() {
        return jdbcTemplate.query("select * from rights",
                new RightRowMapper());
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from rights";
        return jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
    }

    @Transactional
    public void saveRightsForRole(Role role) {
        Set<Integer> rightIds = role.getRightIds();
        SqlParameterSource[] params = new SqlParameterSource[rightIds.size()];
        int i = 0;
        for (Integer rId : rightIds) {
            params[i] = new MapSqlParameterSource("roleId", role.getId()).addValue("rightId", rId);
            i++;
        }

        jdbcTemplate.update("DELETE from roles_rights WHERE role_id = :id", Map.of("id", role.getId()));
        jdbcTemplate.batchUpdate("INSERT INTO roles_rights (role_id, rights_id) VALUES (:roleId, :rightId)", params);
    }

    static class RightRowMapper implements RowMapper<Right>
    {
        @Override
        public Right mapRow(ResultSet rs, int rowNum) throws SQLException {
            Right right = new Right();
            right.setId(rs.getInt("id"));
            right.setName(rs.getString("name"));
            right.setDescription(rs.getString("description"));
            return right;
        }
    }
}
