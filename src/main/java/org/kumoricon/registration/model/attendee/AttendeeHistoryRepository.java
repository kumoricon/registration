package org.kumoricon.registration.model.attendee;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AttendeeHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public AttendeeHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public AttendeeHistory findById(int id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from attendeehistory where id=? order by timestamp desc",
                    new Object[]{id}, new AttendeeHistoryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeHistory> findAllByAttendeeId(int id) {
        try {
            return jdbcTemplate.query(
                    "select * from attendeehistory where attendee_id = ? order by timestamp desc",
                    new Object[]{id}, new AttendeeHistoryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    @Transactional
    public void save(AttendeeHistory attendeeHistory) {
        if (attendeeHistory.getId() == null) {
            jdbcTemplate.update("INSERT INTO attendeehistory(message, timestamp, user_id, attendee_id) VALUES(?,?,?,?)",
                    attendeeHistory.getMessage(), Timestamp.from(attendeeHistory.getTimestamp()), attendeeHistory.getUserId(), attendeeHistory.getAttendeeId());
        } else {
            jdbcTemplate.update("UPDATE attendeehistory SET message = ?, timestamp = ?, user_id = ?, attendee_id = ? WHERE id = ?",
                    attendeeHistory.getMessage(),
                    Timestamp.from(attendeeHistory.getTimestamp()),
                    attendeeHistory.getUserId(),
                    attendeeHistory.getAttendeeId(),
                    attendeeHistory.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<CheckInByUserDTO> checkInCountByUsers() {
        String sql = "SELECT users.first_name, users.last_name, COUNT(attendeehistory.id) as count FROM attendeehistory JOIN users ON attendeehistory.user_id = users.id WHERE attendeehistory.message='Attendee Checked In' AND timestamp >= (NOW() - (15 * interval '1 minute')) AND attendeehistory.timestamp <= NOW() GROUP BY user_id, first_name, last_name";

        try {
            return jdbcTemplate.query(sql, new CheckInByUserDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void saveAll(List<AttendeeHistory> notes) {
        for (AttendeeHistory ah : notes) {
            save(ah);
        }
    }

    class AttendeeHistoryRowMapper implements RowMapper<AttendeeHistory>
    {
        @Override
        public AttendeeHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            AttendeeHistory ah = new AttendeeHistory();
            ah.setId(rs.getInt("id"));
            ah.setMessage(rs.getString("message"));
            ah.setTimestamp(rs.getTimestamp("timestamp"));
            ah.setUserId(rs.getInt("user_id"));
            ah.setAttendeeId(rs.getInt("attendee_id"));
            return ah;
        }
    }


    private class CheckInByUserDTORowMapper implements RowMapper<CheckInByUserDTO> {
        @Override
        public CheckInByUserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CheckInByUserDTO(
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getInt("count"));
        }
    }

}
