package org.kumoricon.registration.model.order;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {
    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    public List<Payment> findByTillSessionIdAndPaymentType(Integer tillSessionId, Payment.PaymentType paymentType) {
        return jdbcTemplate.query("select * from payments where till_session_id = ? and payment_type = ?",
                new Object[]{tillSessionId, paymentType.getValue()},
                new PaymentRowMapper());
    }


    @Transactional(readOnly = true)
    public BigDecimal getTotalForSessionId(Integer id) {
        String sql = "select sum(amount) from payments WHERE till_session_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, BigDecimal.class);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalByPaymentTypeForSessionId(Integer id, Integer paymentType) {
        String sql = "select sum(amount) from payments WHERE session_id = ? AND payment_type = ?";
        return jdbcTemplate.queryForObject(sql,
                new Object[]{id, paymentType}, BigDecimal.class);
    }



    @Transactional
    public void deleteById(Integer id) {
        if (id != null) {
            jdbcTemplate.update("DELETE FROM payments WHERE id = ?", id);
        }
    }

    @Transactional
    public void save(Payment payment) {
        if (payment.getId() == null) {
            jdbcTemplate.update("INSERT INTO payments (amount, auth_number, payment_location, payment_taken_at, payment_taken_by, payment_type, till_session_id, order_id) VALUES(?,?,?,?,?,?,?,?)",
                    payment.getAmount(), payment.getAuthNumber(), payment.getPaymentLocation(), payment.getPaymentTakenAt(),
                    payment.getPaymentTakenBy(), payment.getPaymentType(), payment.getTillSessionId(), payment.getOrderId());
        } else {
            jdbcTemplate.update("UPDATE payments SET amount = ?, auth_number = ?, payment_location = ?, payment_taken_at = ?, payment_taken_by = ?, payment_type = ?, till_session_id = ?, order_id = ? WHERE id = ?",
                    payment.getAmount(), payment.getAuthNumber(), payment.getPaymentLocation(), payment.getPaymentTakenAt(),
                    payment.getPaymentTakenBy(), payment.getPaymentType(), payment.getTillSessionId(), payment.getOrderId(),
                    payment.getId());
        }
    }

    @Transactional(readOnly = true)
    public List<Payment> findByOrderId(Integer orderId) {
        try {
            return jdbcTemplate.query("select * from payments where order_id = ?",
                    new Object[]{orderId},
                    new PaymentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private class PaymentRowMapper implements RowMapper<Payment>
    {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment p = new Payment();
            p.setId(rs.getInt("id"));
            p.setAmount(rs.getBigDecimal("amount"));
            p.setAuthNumber(rs.getString("auth_number"));
            p.setPaymentLocation(rs.getString("payment_location"));
            p.setPaymentTakenAt(rs.getTimestamp("payment_taken_at").toInstant());
            p.setPaymentTakenBy(rs.getInt("payment_taken_by"));
            p.setTillSessionId(rs.getInt("till_session_id"));
            p.setOrderId(rs.getInt("order_id"));
            return p;
        }
    }


}