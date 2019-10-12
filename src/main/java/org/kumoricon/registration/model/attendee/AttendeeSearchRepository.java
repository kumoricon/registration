package org.kumoricon.registration.model.attendee;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_COLUMNS = "select attendees.id, attendees.order_id, first_name, last_name, legal_first_name, legal_last_name, fan_name, birth_date, checked_in, check_in_time, badges.name as badge_type, paid_amount ";

    public AttendeeSearchRepository(JdbcTemplate jdbcTemplate) {
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
        String searchString = buildSearchString(searchWords);
        String sqlMulti = SELECT_COLUMNS + "from attendees " +
                "join badges on attendees.badge_id = badges.id where " +
                "(first_name similar to ? and last_name similar to ?) or " +
                "(legal_first_name similar to ? and legal_last_name similar to ?) or " +
                "fan_name similar to ? order by attendees.first_name, attendees.last_name";
        String sqlSingle = SELECT_COLUMNS + "from attendees " +
                "join badges on attendees.badge_id = badges.id where " +
                "first_name similar to ? or last_name similar to ? or " +
                "legal_first_name similar to ? or legal_last_name similar to ? or " +
                "fan_name similar to ? order by attendees.first_name, attendees.last_name";

        try {
            String sql = searchWords.length == 1 ? sqlSingle : sqlMulti;
            return jdbcTemplate.query(
                    sql,
                    new Object[]{searchString, searchString, searchString, searchString, searchString}, new AttendeeListDTORowMapper());
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

        try {
            return jdbcTemplate.query(
                    sql,
                    new Object[]{orderId}, new AttendeeListDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeListDTO> searchByBadgeType(Integer badgeId, Integer page) {
        try {
            return jdbcTemplate.query(SELECT_COLUMNS + "from attendees JOIN badges on attendees.badge_id = badges.id WHERE badge_id = ? order by attendees.first_name, attendees.last_name LIMIT ? OFFSET ?",
                    new Object[]{badgeId, 20, 20*page},
                    new AttendeeListDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeListDTO> findAllByOrderId(Integer orderId) {
        try {
            return jdbcTemplate.query(SELECT_COLUMNS + "from attendees JOIN badges on attendees.badge_id = badges.id WHERE order_id = ? order by attendees.first_name, attendees.last_name",
                    new Object[]{orderId},
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

    private String buildSearchString(String[] words) {
        List<String> tmpWords = new ArrayList<>();
        for (String word : words) {
            tmpWords.add("(" + word + ")");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(String.join("|", tmpWords));
        sb.append(")%");

        return sb.toString();
    }

    @SuppressWarnings("Duplicates")
    private class AttendeeListDTORowMapper implements RowMapper<AttendeeListDTO> {
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
            return a;
        }
    }
}
