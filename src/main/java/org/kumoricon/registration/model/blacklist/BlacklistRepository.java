package org.kumoricon.registration.model.blacklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class BlacklistRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BlacklistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    public List<BlacklistName> findAll() {
        return jdbcTemplate.query("select * from blacklist",
                new BlacklistRowMapper());
    }

    @Transactional(readOnly=true)
    public Optional<BlacklistName> findById(int id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "select * from blacklist where id=?",
                    new Object[]{id}, new BlacklistRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Transactional
    public void delete(BlacklistName blacklistName) {
        if (blacklistName.getId() != null) {
            jdbcTemplate.update("DELETE FROM blacklist WHERE id = ?", blacklistName.getId());
        }
    }

    @Transactional
    public void save(BlacklistName blacklistName) {
        if (blacklistName.getId() == null) {
            jdbcTemplate.update("INSERT INTO blacklist(first_name, last_name) VALUES(?,?)",
                    blacklistName.getFirstName(), blacklistName.getLastName());
        } else {
            jdbcTemplate.update("UPDATE blacklist SET first_name = ?, last_name = ? WHERE id = ?",
                    blacklistName.getFirstName(),
                    blacklistName.getLastName(),
                    blacklistName.getId());
        }
    }

    @Transactional(readOnly = true)
    public Integer numberOfMatches(String firstName, String lastName) {
        String sql = "select count(*) from blacklist WHERE first_name = ? and last_name = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    class BlacklistRowMapper implements RowMapper<BlacklistName>
    {
        @Override
        public BlacklistName mapRow(ResultSet rs, int rowNum) throws SQLException {
            BlacklistName b = new BlacklistName();
            b.setId(rs.getInt("id"));
            b.setFirstName(rs.getString("first_name"));
            b.setLastName(rs.getString("last_name"));
            return b;
        }
    }

}
