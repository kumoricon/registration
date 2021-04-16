package org.kumoricon.registration.model.attendee;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class AttendeeHistoryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AttendeeHistoryRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<AttendeeHistoryDTO> findAllDTObyAttendeeId(int id) {
        try {
            return jdbcTemplate.query(
                    """
                    select attendeehistory.*, users.first_name, users.last_name, attendees.first_name as a_first_name, attendees.last_name
                    as a_last_name from attendeehistory join users on attendeehistory.user_id = users.id
                    JOIN attendees on attendeehistory.attendee_id = attendees.id where attendee_id = :id order by timestamp desc
                    """,
                    Map.of("id", id), new AttendeeHistoryDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeHistoryDTO> findAllDTObyOrderId(Integer orderId) {
        try {
            return jdbcTemplate.query(
                    """
                        select attendeehistory.*, users.first_name, users.last_name, attendees.first_name
                        as a_first_name, attendees.last_name as a_last_name from attendeehistory join users on attendeehistory.user_id = users.id
                        JOIN attendees on attendeehistory.attendee_id = attendees.id where attendee_id
                        IN (select id from attendees where attendees.order_id = :orderId) order by timestamp desc
                        """,
                    Map.of("orderId", orderId), new AttendeeHistoryDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void save(AttendeeHistory ah) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(ah);

        if (ah.getId() == null) {
            jdbcTemplate.update("""
                                    INSERT INTO attendeehistory(message, timestamp, user_id, attendee_id)
                                    VALUES(:message, :timestamp, :userId, :attendeeId)
                                    """, params);
        } else {
            jdbcTemplate.update("""
                                    UPDATE attendeehistory set message=:message, timestamp=:timestamp,
                                    user_id=:userId, attendee_id=:attendeeId
                                    where attendeehistory.id=:id)
                                    """, params);
        }
    }

    @Transactional(readOnly = true)
    public List<CheckInByUserDTO> checkInCountByUsers() {
        String sql = """
                    SELECT users.first_name, users.last_name,
                    COUNT(attendeehistory.id) as count FROM attendeehistory
                    JOIN users ON attendeehistory.user_id = users.id
                    WHERE attendeehistory.message='Attendee Checked In'
                    AND timestamp >= (NOW() - (15 * interval '1 minute'))
                    AND attendeehistory.timestamp <= NOW()
                    GROUP BY user_id, first_name, last_name
                    """;

        try {
            return jdbcTemplate.query(sql, new CheckInByUserDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void saveAll(List<AttendeeHistory> notes) {
        SqlParameterSource[] params = new SqlParameterSource[notes.size()];
        int i = 0;
        for (AttendeeHistory ah : notes) {
            assert ah.getId() == null : "saveAll only works with objects that have NOT been saved to the database (id=null)";
            params[i] = new BeanPropertySqlParameterSource(ah);
            i++;
        }

        jdbcTemplate.batchUpdate("""
                                      INSERT INTO attendeehistory(message, timestamp, user_id, attendee_id)
                                      VALUES(:message, :timestamp, :userId, :attendeeId)
                                      """, params);
    }

    private static class AttendeeHistoryDTORowMapper implements RowMapper<AttendeeHistoryDTO> {
        @Override
        public AttendeeHistoryDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AttendeeHistoryDTO(
                    rs.getObject("timestamp", OffsetDateTime.class),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("message"),
                    rs.getInt("attendee_id"),
                    rs.getString("a_first_name") + " " + rs.getString("a_last_name"));
        }
    }


    private static class CheckInByUserDTORowMapper implements RowMapper<CheckInByUserDTO> {
        @Override
        public CheckInByUserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CheckInByUserDTO(
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getInt("count"));
        }
    }

}
