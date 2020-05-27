package org.kumoricon.registration.model.order;

import org.kumoricon.registration.exceptions.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class OrderRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private static final Integer PAGE_SIZE = 20;

    public OrderRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Order findById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from orders where id=:id",
                    Map.of("id", id), new OrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Order id " + id + " not found");
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByOrderId(Integer orderId) {
        try {
            BigDecimal result = jdbcTemplate.queryForObject(
                    "select sum(attendees.paid_amount) from attendees where attendees.order_id = :id",
                    Map.of("id", orderId), BigDecimal.class);
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
                            "where o.order_id = :orderNumber",
                    Map.of("orderNumber", orderNumber), BigDecimal.class);
            return result == null ? BigDecimal.ZERO: result;
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }


    @Transactional(readOnly = true)
    public List<Order> findAllBy(int page) {
        return jdbcTemplate.query("select * from orders ORDER BY id desc LIMIT :limit OFFSET :offset",
                Map.of("limit", PAGE_SIZE, "offset", PAGE_SIZE*page),
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

        SqlParameterSource[] params = new SqlParameterSource[orders.size()];
        int i = 0;
        for (Order order : orders) {
            assert order.getId() == null : "saveAll only works with Orders that have NOT been saved to the database (id=null)";
            params[i] = new BeanPropertySqlParameterSource(order);
            i++;
        }
        final String SQL = "INSERT INTO orders (order_id, order_taken_by_user, paid, notes) VALUES" +
                "(:orderId, :orderTakenByUser, :paid, :notes)";

        jdbcTemplate.batchUpdate(SQL, params);
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from orders";
        return jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
    }

    @Transactional
    public Integer save(Order order) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(order);
        if (order.getId() == null) {
            final String SQL = "INSERT INTO orders (order_id, order_taken_by_user, paid, notes) " +
                    "VALUES (:orderId, :orderTakenByUser, :paid, :notes) returning id";
            return jdbcTemplate.queryForObject(SQL, params, Integer.class);
        } else {
            final String SQL = "UPDATE orders SET notes = :notes, order_id = :orderId, order_taken_by_user = :orderTakenByUser, " +
                    "paid =:paid WHERE orders.id = :id RETURNING id";
            return jdbcTemplate.queryForObject(SQL, params, Integer.class);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findAllDTOBy(Integer page) {
        return jdbcTemplate.query("select orders.*, users.username as order_taken_by_username, total_due, total_paid from orders" +
                " LEFT OUTER JOIN (SELECT payments.order_id, sum(payments.amount) as total_paid from payments GROUP BY payments.order_id) as t1 on orders.id = t1.order_id" +
                " LEFT OUTER JOIN (SELECT attendees.order_id, sum(attendees.paid_amount) as total_due from attendees GROUP BY attendees.order_id) as t2 on orders.id = t2.order_id" +
                " LEFT OUTER JOIN users on orders.order_taken_by_user = users.id" +
                " GROUP BY orders.id, username, total_due, total_paid" +
                " ORDER BY id desc LIMIT :limit OFFSET :offset",
                Map.of("limit", PAGE_SIZE, "offset", PAGE_SIZE*page),
                new OrderDTORowMapper());
    }

    public OrderDTO findDTOById(Integer orderId) {
        return jdbcTemplate.queryForObject("select orders.*, users.username as order_taken_by_username, total_due, total_paid from orders" +
                        " LEFT OUTER JOIN (SELECT payments.order_id, sum(payments.amount) as total_paid from payments GROUP BY payments.order_id) as t1 on orders.id = t1.order_id" +
                        " LEFT OUTER JOIN (SELECT attendees.order_id, sum(attendees.paid_amount) as total_due from attendees GROUP BY attendees.order_id) as t2 on orders.id = t2.order_id" +
                        " LEFT OUTER JOIN users on orders.order_taken_by_user = users.id" +
                        " WHERE orders.id = :orderId" +
                        " GROUP BY orders.id, username, total_due, total_paid",
                Map.of("orderId", orderId),
                new OrderDTORowMapper());
    }

    private static class OrderRowMapper implements RowMapper<Order>
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


    private static class OrderDTORowMapper implements RowMapper<OrderDTO>
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