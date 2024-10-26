package org.kumoricon.registration.voucher;

import org.kumoricon.registration.model.staff.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class VoucherRepository {
    private static final Logger log = LoggerFactory.getLogger(VoucherRepository.class);
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public VoucherRepository(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Voucher findByStaffIdOnDate(final Integer id, final LocalDate date) {
        final SqlParameterSource params = new MapSqlParameterSource("id", id)
                .addValue("date", date);
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM voucher WHERE staff_id = :id AND voucher_date = :date", params, new VoucherRowMapper());
        } catch (final EmptyResultDataAccessException e) {
            log.info("Attempted to query voucher with staff id {}, but found nothing.", id);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Voucher> findByAllStaffOnDate(final List<Staff> staff, final LocalDate date) {
        if (staff.isEmpty())
            return List.of();

        final List<Integer> staffIds = staff.stream()
                .map(Staff::getId)
                .toList();
        final SqlParameterSource params = new MapSqlParameterSource("staffIds", staffIds)
                .addValue("date", date);
        try {
            return jdbcTemplate.query("SELECT * FROM voucher WHERE staff_id IN (:staffIds) AND voucher_date = :date", params, new VoucherRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public Integer countOnDate(final LocalDate date) {
        final String sql = "select count(*) from voucher WHERE voucher_date = :date";
        return jdbcTemplate.queryForObject(sql, Map.of("date", date), Integer.class);
    }

    @Transactional
    public void save(final Voucher voucher) {
        final SqlParameterSource params = new MapSqlParameterSource()
                .addValue("staff_id", voucher.getStaffId())
                .addValue("voucher_date", voucher.getVoucherDate())
                .addValue("voucher_by", voucher.getVoucherBy())
                .addValue("voucher_at", voucher.getVoucherAt());

        final String SQL = """
                INSERT INTO voucher(staff_id, voucher_date, voucher_by, voucher_at)
                VALUES(:staff_id, :voucher_date, :voucher_by, :voucher_at)""";
        jdbcTemplate.update(SQL, params);
    }

    @Transactional
    public void delete(final Voucher voucher) {
        final SqlParameterSource params = new MapSqlParameterSource("id", voucher.getId());

        final String SQL = "DELETE FROM voucher WHERE id = :id";
        jdbcTemplate.update(SQL, params);
    }

    private static class VoucherRowMapper implements RowMapper<Voucher> {

        @Override
        public Voucher mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Voucher voucher = new Voucher();
            voucher.setId(rs.getInt("id"));
            voucher.setStaffId(rs.getInt("staff_id"));
            final Date date = rs.getDate("voucher_date");
            voucher.setVoucherDate(date.toLocalDate());
            voucher.setVoucherBy(rs.getString("voucher_by"));
            voucher.setVoucherAt(rs.getObject("voucher_at", OffsetDateTime.class));
            return voucher;
        }
    }
}
