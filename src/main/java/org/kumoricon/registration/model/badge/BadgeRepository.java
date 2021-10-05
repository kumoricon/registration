package org.kumoricon.registration.model.badge;

import org.kumoricon.registration.exceptions.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// This should not be used by classes other than BadgeService, use that to get badges
@Repository
class BadgeRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BadgeRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    List<Badge> findByVisibleTrue() {
        try {
            return jdbcTemplate.query(
                    "select * from badges where visible=true order by id",
                    new BadgeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    List<Badge> findAll() {
        try {
            return jdbcTemplate.query(
                    "select * from badges order by id",
                    new BadgeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public Integer save(Badge badge) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(badge) {
            @Override
            public Object getValue(String paramName) throws IllegalArgumentException {
                Object value = super.getValue(paramName);
                if (value instanceof Enum) {
                    return ((Enum) value).ordinal();
                }

                return value;
            }
        };

        if (badge.getId() == null) {
            KeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update("INSERT INTO badges (badge_type, badge_type_background_color, badge_type_text, " +
                    "name, required_right, visible, warning_message) " +
                    "VALUES (:badgeType, :badgeTypeBackgroundColor, :badgeTypeText, :name, :requiredRight, :visible, " +
                    ":warningMessage)", parameterSource, holder);
            String key = holder.getKeys().get("id").toString();
            if (key != null) {
                return Integer.parseInt(key);
            }
            throw new RuntimeException("Database didn't return a saved row key");
        } else {
            jdbcTemplate.update("UPDATE badges SET badge_type = :badgeType, " +
                    "badge_type_background_color = :badgeTypeBackgroundColor, badge_type_text = :badgeTypeText, " +
                    "name = :name, required_right = :requiredRight, visible = :visible, " +
                    "warning_message = :warningMessage " +
                    "WHERE id = :id", parameterSource);
            return badge.getId();
        }
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from badges";
        return jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
    }

    @Transactional(readOnly = true)
    public Badge findById(Integer badgeId) throws NotFoundException {
        SqlParameterSource params = new MapSqlParameterSource("id", badgeId);
        try {
            return jdbcTemplate.queryForObject("SELECT * from badges where badges.id=:id",
                    params, new BadgeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Badge id " + badgeId + " not found");
        }
    }

    @Transactional(readOnly = true)
    public Badge findByBadgeName(String membershipType) throws NotFoundException {
        SqlParameterSource params = new MapSqlParameterSource("name", membershipType);
        try {
            return jdbcTemplate.queryForObject("SELECT * from badges where badges.name ilike :name",
                    params, new BadgeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Badge named " + membershipType + " not found");
        }
    }


    static class BadgeRowMapper implements RowMapper<Badge> {
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
