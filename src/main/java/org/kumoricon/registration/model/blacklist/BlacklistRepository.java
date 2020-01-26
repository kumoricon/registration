package org.kumoricon.registration.model.blacklist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BlacklistRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public BlacklistRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<BlacklistName> findAll() {
        return jdbcTemplate.query("select * from blacklist",
                new BlacklistRowMapper());
    }

    @Transactional(readOnly = true)
    public BlacklistName findById(int id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject("select * from blacklist where id=:id",
                namedParameters, new BlacklistRowMapper());
    }

    @Transactional
    public void delete(BlacklistName blacklistName) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", blacklistName.getId());
        if (blacklistName.getId() != null) {
            jdbcTemplate.update("DELETE FROM blacklist WHERE id = :id", namedParameters);
        }
    }

    @Transactional
    public void save(BlacklistName blacklistName) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", blacklistName.getId())
                .addValue("first_name", blacklistName.getFirstName())
                .addValue("last_name", blacklistName.getLastName());

        if (blacklistName.getId() == null) {
            jdbcTemplate.update("INSERT INTO blacklist(first_name, last_name) VALUES(:first_name, :last_name) ON CONFLICT DO NOTHING",
                    namedParameters);
        } else {
            jdbcTemplate.update("UPDATE blacklist SET first_name = :first_name, last_name = :last_name WHERE id = :id",
                    namedParameters);
        }
    }

    @Transactional(readOnly = true)
    public Integer numberOfMatches(String firstName, String lastName) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("first_name", firstName)
                .addValue("last_name", lastName);

        String sql = "select count(*) from blacklist WHERE first_name = :first_name and last_name = :last_name";
        return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
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
