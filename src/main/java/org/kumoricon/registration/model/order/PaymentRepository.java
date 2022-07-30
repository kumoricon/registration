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
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class PaymentRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PaymentRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidForOrder(Integer orderId) {
        String sql = "select sum(amount) from payments WHERE order_id = :orderId";
        BigDecimal result = jdbcTemplate.queryForObject(sql, Map.of("orderId", orderId), BigDecimal.class);
        return result == null ? BigDecimal.ZERO : result;
    }

    @Transactional
    public void deleteById(Integer id) {
        if (id != null) {
            jdbcTemplate.update("DELETE FROM payments WHERE id = :id", Map.of("id", id));
        }
    }

    @Transactional
    public void save(Payment payment) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(payment) {
            @Override
            public Object getValue(String paramName) throws IllegalArgumentException {
                Object value = super.getValue(paramName);
                if (value instanceof Payment.PaymentType)  {
                    return ((Payment.PaymentType) value).getValue();
                }
                if (value instanceof Enum) {
                    return ((Enum) value).ordinal();
                }
                return value;
            }
        };

        if (payment.getId() == null) {
            final String SQL ="INSERT INTO payments " +
                    "(amount, square_receipt_number, check_number, payment_location, payment_taken_at, payment_taken_by, payment_type, till_session_id, order_id) " +
                    "VALUES(:amount,:squareReceiptNumber,:checkNumber,:paymentLocation,:paymentTakenAt,:paymentTakenBy,:paymentType,:tillSessionId,:orderId)";
            jdbcTemplate.update(SQL, params);
        } else {
            final String SQL = "UPDATE payments SET amount = :amount, square_receipt_number = :squareReceiptNumber, check_number = :check_number" +
                    "payment_location = :paymentLocation, payment_taken_at = :paymentTakenAt, " +
                    "payment_taken_by = :paymentTakenBy, payment_type = :paymentType, " +
                    "till_session_id = :tillSessionId, order_id = :orderId WHERE id = :id";
            jdbcTemplate.update(SQL, params);
        }
    }

    @Transactional(readOnly = true)
    public List<Payment> findByOrderId(Integer orderId) {
        try {
            return jdbcTemplate.query("select * from payments where order_id = :orderId",
                    Map.of("orderId", orderId),
                    new PaymentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> findDTOByOrderId(Integer orderId) {
        try {
            return jdbcTemplate.query("select payments.*, u.first_name, u.last_name, t.till_name from payments" +
                            "    LEFT OUTER JOIN users u on payments.payment_taken_by = u.id " +
                            "    LEFT OUTER JOIN tillsessions t ON payments.till_session_id = t.id where order_id = :orderId",
                    Map.of("orderId", orderId),
                    new PaymentDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    public void saveAll(List<Payment> payments) {
        for (Payment payment : payments) {
            save(payment);
        }
    }

    public Payment findById(Integer paymentId) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject("select * from payments where id = :paymentId",
                    Map.of("paymentId", paymentId),
                    new PaymentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Payment id " + paymentId + " not found");
        }
    }

    private static class PaymentRowMapper implements RowMapper<Payment>
    {
        @Override
        public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Payment p = new Payment();
            p.setId(rs.getInt("id"));
            p.setAmount(rs.getBigDecimal("amount"));
            p.setSquareReceiptNumber(rs.getString("square_receipt_number"));
            p.setCheckNumber(rs.getString("check_number"));
            p.setPaymentType(Payment.PaymentType.fromInteger(rs.getInt("payment_type")));
            p.setPaymentLocation(rs.getString("payment_location"));
            p.setPaymentTakenAt(rs.getObject("payment_taken_at", OffsetDateTime.class));
            p.setPaymentTakenBy(rs.getInt("payment_taken_by"));
            p.setTillSessionId(rs.getInt("till_session_id"));
            p.setOrderId(rs.getInt("order_id"));
            return p;
        }
    }

    private static class PaymentDTORowMapper implements RowMapper<PaymentDTO>
    {
        @Override
        public PaymentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaymentDTO p = new PaymentDTO();
            p.setId(rs.getInt("id"));
            p.setAmount(rs.getBigDecimal("amount"));
            p.setSquareReceiptNumber(rs.getString("square_receipt_number"));
            p.setCheckNumber(rs.getString("check_number"));
            p.setPaymentType(Payment.PaymentType.fromInteger(rs.getInt("payment_type")));
            p.setPaymentLocation(rs.getString("payment_location"));
            p.setPaymentTakenAt(rs.getObject("payment_taken_at", OffsetDateTime.class));
            p.setPaymentTakenBy(rs.getInt("payment_taken_by"));
            p.setPaymentTakenByUsername(rs.getString("first_name") + " " + rs.getString("last_name"));
            p.setTillName(rs.getString("till_name"));
            p.setTillSessionId(rs.getInt("till_session_id"));
            p.setOrderId(rs.getInt("order_id"));
            return p;
        }
    }

}