package org.kumoricon.registration.model.order;

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
public class OrderHandOffRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderHandOffRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<OrderHandOffDTO> findAll() {
        try {
            return jdbcTemplate.query("select orderhandoffs.*, u.username from orderhandoffs join users u on orderhandoffs.user_id = u.id",
                    new OrderHandOffRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void deleteHandOff(Integer orderId) {
        assert orderId != null;
        final String SQL = "delete from orderhandoffs where order_id = :orderId";
        jdbcTemplate.update(SQL, Map.of("orderId", orderId));
    }

    @Transactional
    public void saveHandOff(OrderHandOffDTO orderHandOffDTO) {
        assert orderHandOffDTO != null;
        SqlParameterSource params = new BeanPropertySqlParameterSource(orderHandOffDTO);
        final String SQL = "INSERT INTO orderhandoffs (order_id, user_id, timestamp, stage) VALUES " +
                "(:orderId, :fromUserId, :timestamp, :stage) ON CONFLICT (order_id) " +
                " DO UPDATE SET user_id=:fromUserId, timestamp=:timestamp, stage=:stage";
        jdbcTemplate.update(SQL, params);
    }

    public OrderHandOffDTO findByOrderId(Integer orderId) {
        return jdbcTemplate.queryForObject("select orderhandoffs.*, u.username from orderhandoffs join users u on orderhandoffs.user_id = u.id WHERE order_id = :orderId",
                Map.of("orderId", orderId), new OrderHandOffRowMapper());
    }


    private static class OrderHandOffRowMapper implements RowMapper<OrderHandOffDTO>
    {
        @Override
        public OrderHandOffDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OrderHandOffDTO(
                    rs.getInt("order_id"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getObject("timestamp", OffsetDateTime.class),
                    rs.getString("stage"));
        }
    }
}