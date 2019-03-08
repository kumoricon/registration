package org.kumoricon.registration.model.role;


import org.kumoricon.registration.model.user.User;
import org.kumoricon.registration.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RightRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RightRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    public Right findByNameIgnoreCase(String name) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from rights where name=?",
                    new Object[]{name}, new RightRepository.RightRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Set<Right> findAllRightsByUserId(Integer id) {
        try {
            List<Right> data = jdbcTemplate.query(
                    "select * from rights where rights.id in (select roles_rights.rights_id from roles_rights join users on roles_rights.role_id = users.role_id where users.id = ? ); ",
                    new Object[]{id}, new RightRowMapper());

            return new HashSet<>(data);
        } catch (EmptyResultDataAccessException e) {
            return new HashSet<>();
        }

    }

    @Transactional
    public void save(Right right) {
        if (right.getId() == null) {
            jdbcTemplate.update("INSERT INTO rights " +
                            "(name, description) " +
                            "VALUES(?,?)",
                    right.getName(), right.getDescription());
        } else {
            jdbcTemplate.update("UPDATE rights SET name = ?, description = ? WHERE id = ?",
                    right.getName(), right.getDescription(), right.getId());
        }
    }

    @Transactional(readOnly=true)
    public List<Right> findAll() {
        return jdbcTemplate.query("select * from rights",
                new RightRowMapper());
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from rights";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    class RightRowMapper implements RowMapper<Right>
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
