package org.kumoricon.registration.model.tillsession;

import org.kumoricon.registration.model.order.Payment;
import org.kumoricon.registration.model.user.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.kumoricon.registration.model.SqlHelper.translate;

/**
 * Unlike most repositories, Till Session access should only happen through SessionService
 */

@Repository
public class TillSessionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ZoneId timezone;

    public TillSessionRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.timezone = ZoneId.of("America/Los_Angeles");
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional
    public TillSession save(TillSession tillSession) {
        SqlParameterSource params = new MapSqlParameterSource("id", tillSession.getId())
                .addValue("end_time", translate(tillSession.getEndTime()))
                .addValue("open", tillSession.isOpen())
                .addValue("start_time", translate(tillSession.getStartTime()))
                .addValue("user_id", tillSession.getUserId())
                .addValue("till_name", tillSession.getTillName());

        if (tillSession.getId() == null) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getJdbcTemplate());
            jdbcInsert.withTableName("tillsessions").usingGeneratedKeyColumns("id");
            Number key = jdbcInsert.executeAndReturnKey(params);
            tillSession.setId(key.intValue());

        } else {
            final String SQL ="UPDATE tillsessions SET end_time = :end_time, open = :open, start_time = :start_time, " +
                    "user_id = :user_id, till_name = :till_name WHERE id = :id";
            jdbcTemplate.update(SQL, params);
        }
        return tillSession;
    }


    @Transactional(readOnly = true)
    TillSession findOneById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * from tillsessions where tillsessions.id=:id",
                    Map.of("id", id), new TillSessionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    TillSession getOpenSessionForUser(User user) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * from tillsessions where tillsessions.open = true and tillsessions.user_id=:id",
                    Map.of("id", user.getId()), new TillSessionRowMapper());
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
                    "SELECT tillsessions.*, users.first_name, users.last_name, sum(payments.amount) as total from tillsessions join users on tillsessions.user_id = users.id join payments on payments.till_session_id = tillsessions.id GROUP BY tillsessions.id, users.first_name, users.last_name, tillsessions.end_time order by tillsessions.end_time desc",
                    new TillSessionDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    List<TillSessionDTO> findOpenTillSessionDTOs() {
        try {
            return jdbcTemplate.query(
                    "SELECT tillsessions.*, users.first_name, users.last_name, sum(payments.amount) as total from tillsessions join users on tillsessions.user_id = users.id join payments on payments.till_session_id = tillsessions.id WHERE open is true GROUP BY tillsessions.id, users.first_name, users.last_name, tillsessions.end_time order by tillsessions.end_time",
                    new TillSessionDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    TillSessionDTO getOpenTillSessionDTOforUser(User user) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT tillsessions.*, users.first_name, users.last_name, sum(payments.amount) as total from tillsessions join users on tillsessions.user_id = users.id join payments on payments.till_session_id = tillsessions.id where tillsessions.open = true and tillsessions.user_id=:id GROUP BY tillsessions.id, users.first_name, users.last_name",
                    Map.of("id", user.getId()),
                    new TillSessionDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    TillSessionDTO getTillSessionDTOById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT tillsessions.*, users.first_name, users.last_name, sum(payments.amount) as total from tillsessions join users on tillsessions.user_id = users.id join payments on payments.till_session_id = tillsessions.id where tillsessions.id=:id GROUP BY tillsessions.id, users.first_name, users.last_name",
                    Map.of("id", id),
                    new TillSessionDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<TillSessionDetailDTO.TillSessionPaymentTotalDTO> getPaymentTotals(Integer id) {
        try {
            return jdbcTemplate.query(
                    "select payment_type, sum(amount) as total from payments where till_session_id = :tillSessionId group by payment_type order by payment_type",
                    Map.of("tillSessionId", id),
                    new TillSessionPaymentTotalDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<TillSessionDetailDTO.TillSessionBadgeCountDTO> getBadgeCounts(Integer id) {
        final String sql = "select b.name," +
                "  (CASE" +
                "     WHEN extract(year from age(now() at time zone 'America/Los_Angeles', attendees.birth_date)) >= 13 THEN" +
                "       'Adult/Youth'" +
                "    WHEN extract(year from age(now() at time zone 'America/Los_Angeles', attendees.birth_date)) >= 6 THEN" +
                "       'Child'" +
                "     ELSE" +
                "       'Under 6'" +
                "    END) as ageRange," +
                "       count(*)" +
                " from attendees" +
                "       JOIN orders o on attendees.order_id = o.id" +
                "       JOIN badges b on attendees.badge_id = b.id" +
                " WHERE o.id in (select order_id from payments where till_session_id = :id)" +
                " GROUP BY b.name, ageRange" +
                " ORDER BY b.name, ageRange;";
        try {
            return jdbcTemplate.query(
                    sql,
                    Map.of("id", id),
                    new TillSessionBadgeCountDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<TillSessionDetailDTO.TillSessionOrderDTO> getOrderDetails(Integer id) {
        final String sql = "select t1.id, badges, payments from (" +
 //               "-- select badges in order" +
                "select id, array_to_string(array_agg(name || ' ' || ageRange || ': ' || cnt), ', ') as badges from (" +
                "  select" +
                "   o.id," +
                "   b.name," +
                "   (CASE" +
                "      WHEN extract(year from age(now() at time zone 'America/Los_Angeles', attendees.birth_date)) >= 13 THEN" +
                "        'Adult'" +
                "      WHEN extract(year from age(now() at time zone 'America/Los_Angeles', attendees.birth_date)) >= 6 THEN" +
                "        'Child'" +
                "      ELSE" +
                "        'Under 6'" +
                "     END) as ageRange," +
                "   count(*) as cnt" +
                "  from attendees" +
                "        JOIN orders o on attendees.order_id = o.id" +
                "        JOIN badges b on attendees.badge_id = b.id" +
                "  WHERE o.id in (select order_id from payments where till_session_id = :id)" +
                "  GROUP BY o.id, b.name, ageRange" +
                "  ORDER BY o.id, b.name, ageRange) as counts" +
                " GROUP BY id) as t1" +
                "  left outer join (" +
//                "-- select payments in order" +
                "select id, array_to_string(array_agg(taken_at || ' - ' || type || auth || ': $' || amount), ', ') as payments from (" +
                "select" +
                "  o.id, date_trunc('second', p.payment_taken_at AT TIME ZONE 'America/Los_Angeles') as taken_at, " +
                "  (CASE" +
                "    WHEN p.payment_type = 0 THEN '" + Payment.PaymentType.CASH + "'" +
                "    WHEN p.payment_type = 1 THEN '" + Payment.PaymentType.CHECK + "'" +
                "    WHEN p.payment_type = 2 THEN '" + Payment.PaymentType.CREDIT + "'" +
                "      WHEN p.payment_type = 3 THEN '" + Payment.PaymentType.PREREG + "'" +
                "    END) as type," +
                "   (CASE\n" +
                "    WHEN p.auth_number is null then ''\n" +
                "    ELSE ' (Auth ' || p.auth_number || ')'\n" +
                "    END\n" +
                "    ) as auth," +
                "  p.amount" +
                "  from orders o" +
                "       JOIN payments p on o.id = p.order_id" +
                "  WHERE p.till_session_id = :id" +
                "  ORDER BY o.id, p.payment_type) as a" +
                " group by id) as t2 on t1.id = t2.id;";

        try {
            return jdbcTemplate.query(
                    sql,
                    Map.of("id", id),
                    new TillSessionOrderDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }

    }


    static class TillSessionRowMapper implements RowMapper<TillSession>
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
            tillSession.setTillName(rs.getString("till_name"));
            return tillSession;
        }
    }

    class TillSessionDTORowMapper implements RowMapper<TillSessionDTO>
    {
        @Override
        public TillSessionDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TillSessionDTO t = new TillSessionDTO();
            t.setId(rs.getInt("id"));

            Timestamp start = rs.getTimestamp("start_time");
            Timestamp end = rs.getTimestamp("end_time");
            t.setStartTime(start == null ? null: start.toInstant().atZone(timezone));
            t.setEndTime(end == null ? null: end.toInstant().atZone(timezone));

            t.setOpen(rs.getBoolean("open"));
            t.setUserId(rs.getInt("user_id"));
            t.setUsername(rs.getString("first_name") + " " + rs.getString("last_name"));
            t.setTotal(rs.getBigDecimal("total"));
            t.setTillName(rs.getString("till_name"));
            return t;
        }
    }

    static class TillSessionPaymentTotalDTORowMapper implements RowMapper<TillSessionDetailDTO.TillSessionPaymentTotalDTO> {
        @Override
        public TillSessionDetailDTO.TillSessionPaymentTotalDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TillSessionDetailDTO.TillSessionPaymentTotalDTO(
                    Payment.PaymentType.fromInteger(rs.getInt("payment_type")).toString(),
                    rs.getBigDecimal("total")
            );
        }
    }

    static class TillSessionOrderDTORowMapper implements RowMapper<TillSessionDetailDTO.TillSessionOrderDTO> {
        @Override
        public TillSessionDetailDTO.TillSessionOrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TillSessionDetailDTO.TillSessionOrderDTO(
                    rs.getInt("id"),
                    rs.getString("badges"),
                    rs.getString("payments")
            );
        }

    }

    static class TillSessionBadgeCountDTORowMapper implements RowMapper<TillSessionDetailDTO.TillSessionBadgeCountDTO>
    {
        @Override
        public TillSessionDetailDTO.TillSessionBadgeCountDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TillSessionDetailDTO.TillSessionBadgeCountDTO(
                    rs.getString("name") + " " + rs.getString("agerange"),
                    rs.getInt("count"));
        }
    }

}