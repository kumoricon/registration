package org.kumoricon.registration.model.badge;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
class AgeRangeRepository {
    private final JdbcTemplate jdbcTemplate;

    public AgeRangeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<AgeRange> findAgeRangesForBadgeId(Integer id) {
        try {
            return jdbcTemplate.query(
                    "select * from ageranges where badge_id = ?",
                    new Object[]{id}, new AgeRangeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    public AgeRange findAgeRangeForBadgeIdAndAge(Integer badgeId, Long age) {
        return jdbcTemplate.queryForObject(
                "select * from ageranges where badge_id = ? and ? >= min_age and ? <= max_age",
                new Object[]{badgeId, age, age}, new AgeRangeRowMapper());
    }


    @Transactional
    public void save(List<AgeRange> ageRanges, Integer badgeId) {
        for (AgeRange ageRange : ageRanges) {
            if (ageRange.getId() == null) {
                jdbcTemplate.update("INSERT INTO ageranges(cost, max_age, min_age, name, stripe_color, stripe_text, badge_id)" +
                        " VALUES(?,?,?,?,?,?,?)",
                        ageRange.getCost(), ageRange.getMaxAge(), ageRange.getMinAge(), ageRange.getName(),
                        ageRange.getStripeColor(), ageRange.getStripeText(), badgeId);
            } else {
                jdbcTemplate.update("UPDATE ageranges SET cost = ?, max_age = ?, min_age = ?, name = ?, stripe_color = ?, stripe_text = ?, badge_id = ? WHERE id = ?",
                    ageRange.getCost(),
                        ageRange.getMaxAge(),
                        ageRange.getMinAge(),
                        ageRange.getName(),
                        ageRange.getStripeColor(),
                        ageRange.getStripeText(),
                        badgeId,
                        ageRange.getId());
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