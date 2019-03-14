package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.user.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kumoricon.registration.model.SqlHelper.translate;

/**
 * Unlike most repositories, Session access should only happen through SessionService
 */

@Service
public class TillSessionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TillSessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional
    public TillSession save(TillSession tillSession) {
        if (tillSession.getId() == null) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName("tillsessions").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("end_time", translate(tillSession.getEndTime()));
            parameters.put("open", tillSession.isOpen());
            parameters.put("start_time", translate(tillSession.getStartTime()));
            parameters.put("user_id", tillSession.getUserId());
            Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
            tillSession.setId(key.intValue());
            return tillSession;

        } else {
            jdbcTemplate.update("UPDATE tillsessions SET end_time = ?, open = ?, start_time = ?, user_id = ? WHERE id = ?",
                    translate(tillSession.getEndTime()), tillSession.isOpen(), translate(tillSession.getStartTime()), tillSession.getUserId(), tillSession.getId());
            return tillSession;
        }
    }


    @Transactional(readOnly = true)
    TillSession findOneById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * from tillsessions where tillsessions.id=?",
                    new Object[]{id}, new TillSessionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    TillSession getOpenSessionForUser(User user) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * from tillsessions where tillsessions.open = true and tillsessions.user_id=?",
                    new Object[]{user.getId()}, new TillSessionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    List<TillSession> findAllOpenSessions() {
        try {
            return jdbcTemplate.query(
                    "SELECT * from tillsessions where tillsessions.open = true",
                    new TillSessionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    List<TillSession> findAllOrderByEnd() {
        try {
            return jdbcTemplate.query(
                    "SELECT * from tillsessions ORDER BY tillsessions.end_time DESC",
                    new TillSessionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    List<TillSessionDTO> findAllTillSessionDTO() {
        try {
            return jdbcTemplate.query(
                    "SELECT tillsessions.*, users.username, sum(payments.amount) as total from tillsessions join users on tillsessions.user_id = users.id join payments on payments.till_session_id = tillsessions.id GROUP BY tillsessions.id, users.username order by tillsessions.end_time",
                    new TillSessionDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }



    class TillSessionRowMapper implements RowMapper<TillSession>
    {
        @Override
        public TillSession mapRow(ResultSet rs, int rowNum) throws SQLException {
            TillSession tillSession = new TillSession();
            tillSession.setId(rs.getInt("id"));
            Timestamp start = rs.getTimestamp("start_time");
            if (start != null) {
                tillSession.setStartTime(start.toInstant());
            } else {
                tillSession.setStartTime(null);
            }
            tillSession.setOpen(rs.getBoolean("open"));
            Timestamp end = rs.getTimestamp("end_time");
            if (end != null) {
                tillSession.setEndTime(end.toInstant());
            } else {
                tillSession.setEndTime(null);
            }
            tillSession.setUserId(rs.getInt("user_id"));
            return tillSession;
        }
    }

    class TillSessionDTORowMapper implements RowMapper<TillSessionDTO>
    {
        @Override
        public TillSessionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TillSessionDTO t = new TillSessionDTO();
            t.setId(rs.getInt("id"));
            t.setStartTime(rs.getTimestamp("start_time"));
            t.setOpen(rs.getBoolean("open"));
            t.setEndTime(rs.getTimestamp("end_time"));
            t.setUserId(rs.getInt("user_id"));
            t.setUsername(rs.getString("username"));
            t.setTotal(rs.getBigDecimal("total"));
            return t;
        }
    }

}