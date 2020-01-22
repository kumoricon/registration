package org.kumoricon.registration.model.attendee;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AttendeeSearchRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final String SELECT_COLUMNS = "select attendees.id, attendees.order_id, first_name, last_name, legal_first_name, legal_last_name, fan_name, birth_date, checked_in, check_in_time, badges.name as badge_type, paid_amount, membership_revoked ";

    public AttendeeSearchRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Searches for Attendees that contain all the given words in the firstName,
     * lastName, legalFirstName, legalLastName or fanName fields, case insensitive
     * @param searchWords Words to search for
     * @return Matching Attendees
     */
    @Transactional(readOnly = true)
    public List<AttendeeListDTO> searchFor(String[] searchWords) {
        String sqlMulti = SELECT_COLUMNS + "from attendees " +
                "join badges on attendees.badge_id = badges.id where " +
                "(first_name ILIKE :term0 || '%' and last_name ILIKE :term1 || '%') or " +
                "(legal_first_name ILIKE :term0 || '%' and legal_last_name ILIKE :term1 || '%') or " +
                "fan_name ILIKE '%' || :term0 || '%' AND fan_name ILIKE '%' || :term1 || '%' order by attendees.first_name, attendees.last_name";
        String sqlSingle = SELECT_COLUMNS + "from attendees " +
                "join badges on attendees.badge_id = badges.id where " +
                "first_name ILIKE :term0 || '%' or last_name ILIKE :term0 || '%' or " +
                "legal_first_name ILIKE :term0 || '%' or legal_last_name ILIKE :term0 || '%' or " +
                "fan_name ILIKE '%' || :term0 || '%' order by attendees.first_name, attendees.last_name";

        MapSqlParameterSource params = new MapSqlParameterSource();
        for (int i = 0; i < searchWords.length; i++) {
            params.addValue("term" + i, searchWords[i]);
        }

        try {
            if (searchWords.length >= 2) {
                return jdbcTemplate.query(sqlMulti, params, new AttendeeListDTORowMapper());
            } else {
                return jdbcTemplate.query(sqlSingle, params, new AttendeeListDTORowMapper());
            }
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeListDTO> searchByOrderNumber(String orderId) {
        String sql = SELECT_COLUMNS + ", orders.order_id from attendees " +
                "join orders on attendees.order_id = orders.id " +
                "join badges on attendees.badge_id = badges.id " +
                "where orders.order_id = ? order by attendees.first_name, attendees.last_name";

        MapSqlParameterSource params = new MapSqlParameterSource("orderId", orderId);

        try {
            return jdbcTemplate.query(
                    sql, params, new AttendeeListDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeListDTO> searchByBadgeType(Integer badgeId, Integer page) {
        MapSqlParameterSource params = new MapSqlParameterSource("badgeId", badgeId);
        params.addValue("offset", page*20);
        params.addValue("limit", 20);

        try {
            return jdbcTemplate.query(SELECT_COLUMNS + "from attendees JOIN badges on attendees.badge_id = badges.id WHERE badge_id = :badgeId order by attendees.first_name, attendees.last_name LIMIT :limit OFFSET :offset",
                    params,
                    new AttendeeListDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeListDTO> findAllByOrderId(Integer orderId) {
        MapSqlParameterSource params = new MapSqlParameterSource("orderId", orderId);

        try {
            return jdbcTemplate.query(SELECT_COLUMNS + "from attendees JOIN badges on attendees.badge_id = badges.id WHERE order_id = :orderId order by attendees.first_name, attendees.last_name",
                    params,
                    new AttendeeListDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeListDTO> findAll() {
        try {
            return jdbcTemplate.query(SELECT_COLUMNS + "from attendees JOIN badges on attendees.badge_id = badges.id order by attendees.first_name, attendees.last_name",
                    new AttendeeListDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("Duplicates")
    private static class AttendeeListDTORowMapper implements RowMapper<AttendeeListDTO> {
        @Override
        public AttendeeListDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AttendeeListDTO a = new AttendeeListDTO();
            a.setId(rs.getInt("id"));
            Date birthDate = rs.getDate("birth_date");
            if (birthDate != null) {
                a.setBirthDate(birthDate.toLocalDate());
            } else {
                a.setBirthDate(null);
            }
            Timestamp checkInTime = rs.getTimestamp("check_in_time");
            if (checkInTime != null) {
                a.setCheckInTime(checkInTime.toInstant());
            } else {
                a.setCheckInTime(null);
            }

            a.setCheckedIn(rs.getBoolean("checked_in"));
            a.setFanName(rs.getString("fan_name"));
            a.setFirstName(rs.getString("first_name"));
            a.setLastName(rs.getString("last_name"));
            a.setLegalFirstName(rs.getString("legal_first_name"));
            a.setLegalLastName(rs.getString("legal_last_name"));
            a.setOrderId(rs.getInt("order_id"));
            a.setBadgeType(rs.getString("badge_type"));
            a.setPaidAmount(rs.getBigDecimal("paid_amount"));
            a.setMembershipRevoked(rs.getBoolean("membership_revoked"));
            return a;
        }
    }
}
