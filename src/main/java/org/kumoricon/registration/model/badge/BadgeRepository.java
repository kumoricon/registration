package org.kumoricon.registration.model.badge;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This should not be used by classes other than BadgeService, use that to get badges
@Repository
class BadgeRepository {
    private final JdbcTemplate jdbcTemplate;

    public BadgeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    List<Badge> findByVisibleTrue() {
        try {
            return jdbcTemplate.query(
                    "select * from badges where visible= true",
                    new BadgeRepository.BadgeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly=true)
    List<Badge> findAll() {
        try {
            return jdbcTemplate.query(
                    "select * from badges",
                    new BadgeRepository.BadgeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public Integer save(Badge badge) {
        if (badge.getId() == null) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName("badges").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("badge_type", badge.getBadgeType());
            parameters.put("badge_type_background_color", badge.getBadgeTypeBackgroundColor());
            parameters.put("badge_type_text", badge.getBadgeTypeText());
            parameters.put("name", badge.getName());
            parameters.put("required_right", badge.getRequiredRight());
            parameters.put("visible", badge.isVisible());
            parameters.put("warning_message", badge.getWarningMessage());
            Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
            return key.intValue();
        } else {
            jdbcTemplate.update("UPDATE badges SET badge_type = ?, badge_type_background_color = ?, badge_type_text = ?, name = ?, required_right = ?, visible = ?, warning_message = ? WHERE id = ?",
                badge.getBadgeType(), badge.getBadgeTypeBackgroundColor(), badge.getBadgeTypeText(), badge.getName(), badge.getRequiredRight(), badge.isVisible(), badge.getWarningMessage(), badge.getId());
            return badge.getId();
        }
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from badges";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    class BadgeRowMapper implements RowMapper<Badge> {
        @Override
        public Badge mapRow(ResultSet rs, int rowNum) throws SQLException {
            Badge badge = new Badge();
            badge.setId(rs.getInt("id"));
            badge.setBadgeType(BadgeType.of(rs.getInt("badge_type")));
            badge.setBadgeTypeBackgroundColor(rs.getString("badge_type_background_color"));
            badge.setBadgeTypeText(rs.getString("badge_type_text"));
            badge.setName(rs.getString("name"));
            badge.setRequiredRight(rs.getString("required_right"));
            badge.setVisible(rs.getBoolean("visible"));
            badge.setWarningMessage(rs.getString("warning_message"));
            return badge;
        }
    }
}
