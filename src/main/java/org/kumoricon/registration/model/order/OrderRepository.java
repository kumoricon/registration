package org.kumoricon.registration.model.order;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Integer PAGE_SIZE = 20;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Order findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from orders where id=?",
                    new Object[]{id}, new OrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByOrderId(Integer orderId) {
        try {
            BigDecimal result = jdbcTemplate.queryForObject(
                    "select sum(attendees.paid_amount) from attendees where attendees.order_id = ?",
                    new Object[]{orderId}, BigDecimal.class);
            return result == null ? BigDecimal.ZERO: result;    // If there are no attendees in the order, the
                                                                // above query will return null. That needs to be
                                                                // sanitized - should be 0.
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByOrderNumber(String orderNumber) {
        try {
            BigDecimal result = jdbcTemplate.queryForObject(
                    "select sum(attendees.paid_amount) from attendees join orders o on attendees.order_id = o.id\n" +
                            "where o.order_id = ?",
                    new Object[]{orderNumber}, BigDecimal.class);
            return result == null ? BigDecimal.ZERO: result;
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }


    @Transactional(readOnly = true)
    public List<Order> findAllBy(Integer page) {
        return jdbcTemplate.query("select * from orders ORDER BY id desc LIMIT ? OFFSET ?",
                new Object[]{PAGE_SIZE, PAGE_SIZE*page},
                new OrderRowMapper());
    }

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        try {
            return jdbcTemplate.query("select * from orders",
                    new OrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void saveAll(List<Order> orders) {
        for (Order order : orders) {
            save(order);
        }
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from orders";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Transactional
    public Integer save(Order order) {
        if (order.getId() == null) {
            SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            jdbcInsert.withTableName("orders").usingGeneratedKeyColumns("id");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("notes", order.getNotes());
            parameters.put("order_id", order.getOrderId());
            parameters.put("order_taken_by_user", order.getOrderTakenByUser());
            parameters.put("paid", order.getPaid());
            Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
            return (key).intValue();
        } else {
            jdbcTemplate.update("UPDATE orders SET notes = ?, order_id = ?, order_taken_by_user = ?, paid =? WHERE id = ?",
                    order.getNotes(), order.getOrderId(), order.getOrderTakenByUser(), order.getPaid(), order.getId());
            return order.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findAllDTOBy(Integer page) {
        return jdbcTemplate.query("select orders.*, users.username as order_taken_by_username, total_due, total_paid from orders" +
                " LEFT OUTER JOIN (SELECT payments.order_id, sum(payments.amount) as total_paid from payments GROUP BY payments.order_id) as t1 on orders.id = t1.order_id" +
                " LEFT OUTER JOIN (SELECT attendees.order_id, sum(attendees.paid_amount) as total_due from attendees GROUP BY attendees.order_id) as t2 on orders.id = t2.order_id" +
                " LEFT OUTER JOIN users on orders.order_taken_by_user = users.id" +
                " GROUP BY orders.id, username, total_due, total_paid" +
                " ORDER BY id desc LIMIT ? OFFSET ?",
                new Object[]{PAGE_SIZE, PAGE_SIZE*page},
                new OrderDTORowMapper());
    }

    public OrderDTO findDTOById(Integer orderId) {
        return jdbcTemplate.queryForObject("select orders.*, users.username as order_taken_by_username, total_due, total_paid from orders" +
                        " LEFT OUTER JOIN (SELECT payments.order_id, sum(payments.amount) as total_paid from payments GROUP BY payments.order_id) as t1 on orders.id = t1.order_id" +
                        " LEFT OUTER JOIN (SELECT attendees.order_id, sum(attendees.paid_amount) as total_due from attendees GROUP BY attendees.order_id) as t2 on orders.id = t2.order_id" +
                        " LEFT OUTER JOIN users on orders.order_taken_by_user = users.id" +
                        " WHERE orders.id = ?" +
                        " GROUP BY orders.id, username, total_due, total_paid",
                new Object[]{orderId},
                new OrderDTORowMapper());
    }

    private class OrderRowMapper implements RowMapper<Order>
    {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order o = new Order();
            o.setId(rs.getInt("id"));
            o.setOrderId(rs.getString("order_id"));
            o.setOrderTakenByUser(rs.getInt("order_taken_by_user"));
            o.setPaid(rs.getBoolean("paid"));
            o.setNotes(rs.getString("notes"));
            return o;
        }
    }


    private class OrderDTORowMapper implements RowMapper<OrderDTO>
    {
        @Override
        public OrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OrderDTO(
                    rs.getInt("id"),
                    rs.getString("order_id"),
                    rs.getBoolean("paid"),
                    rs.getBigDecimal("total_due"),
                    rs.getBigDecimal("total_paid"),
                    rs.getString("order_taken_by_username"),
                    rs.getString("notes")
            );
        }
    }

}