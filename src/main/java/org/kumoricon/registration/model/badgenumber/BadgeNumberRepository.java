package org.kumoricon.registration.model.badgenumber;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
public class BadgeNumberRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BadgeNumberRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void insert(String prefix, Integer number) {
        final String sql = "INSERT INTO badgenumbers (prefix, number) values (:prefix, :number)";
        jdbcTemplate.update(sql, Map.of("prefix", prefix, "number", number));
    }

    @Transactional
    public Integer increment(String prefix) {
        SqlParameterSource params = new MapSqlParameterSource("prefix", prefix);
        final String sql = "UPDATE badgenumbers SET number = number+1 WHERE prefix = :prefix RETURNING number";
        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    @Transactional
    public boolean exists(String prefix) {
        final String sql = "SELECT count(*) FROM badgenumbers WHERE prefix = :prefix";
        Long count = jdbcTemplate.queryForObject(sql, Map.of("prefix", prefix), Long.class);
        if (count == null) {
            return false;
        } else {
            return count > 0;
        }
    }
}
