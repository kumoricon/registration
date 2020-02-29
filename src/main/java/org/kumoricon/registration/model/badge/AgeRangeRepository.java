package org.kumoricon.registration.model.badge;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
class AgeRangeRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AgeRangeRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<AgeRange> findAgeRangesForBadgeId(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        try {
            return jdbcTemplate.query(
                    "select * from ageranges where badge_id = :id", params, new AgeRangeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    public AgeRange findAgeRangeForBadgeIdAndAge(Integer badgeId, Long age) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", badgeId)
                .addValue("age", age);
        return jdbcTemplate.queryForObject(
                "select * from ageranges where badge_id = :id and :age >= min_age and :age <= max_age",
                params, new AgeRangeRowMapper());
    }


    @Transactional
    public void save(List<AgeRange> ageRanges) {
        for (AgeRange ageRange : ageRanges) {
            SqlParameterSource params = new BeanPropertySqlParameterSource(ageRange);
            if (ageRange.getId() == null) {
                jdbcTemplate.update("INSERT INTO ageranges(cost, max_age, min_age, name, stripe_color, stripe_text, badge_id)" +
                        " VALUES(:cost, :maxAge, :minAge, :name, :stripeColor, :stripeText, :badgeId)", params);
            } else {
                jdbcTemplate.update("UPDATE ageranges SET cost = :cost, max_age = :maxAge, min_age = :minAge, " +
                        "name = :name, stripe_color = :stripeColor, stripe_text = :stripeText, badge_id = :badgeId " +
                        "WHERE id = :id", params);
            }
        }
    }

    static class AgeRangeRowMapper implements RowMapper<AgeRange>
    {
        @Override
        public AgeRange mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgeRange ageRange = new AgeRange();
            ageRange.setId(rs.getInt("id"));
            ageRange.setCost(rs.getBigDecimal("cost"));
            ageRange.setMaxAge(rs.getInt("max_age"));
            ageRange.setMinAge(rs.getInt("min_age"));
            ageRange.setName(rs.getString("name"));
            ageRange.setStripeColor(rs.getString("stripe_color"));
            ageRange.setStripeText(rs.getString("stripe_text"));
            return ageRange;
        }
    }



}