package org.kumoricon.registration.model.attendee;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Repository
public class AttendeeDetailRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ZoneId timezone;

    public AttendeeDetailRepository(JdbcTemplate jdbcTemplate) {
        this.timezone = ZoneId.of("America/Los_Angeles");
        this.jdbcTemplate = jdbcTemplate;
    }


    @Transactional(readOnly = true)
    public AttendeeDetailDTO findByIdAndOrderId(int attendeeId, int orderId) {
        return jdbcTemplate.queryForObject(
                "select attendees.*, b.name as badge_type from attendees JOIN badges b on attendees.badge_id = b.id  where attendees.id=? and attendees.order_id = ?",
                new Object[]{attendeeId, orderId}, new AttendeeDetailDTORowMapper());
    }

    @SuppressWarnings("Duplicates")
    private class AttendeeDetailDTORowMapper implements RowMapper<AttendeeDetailDTO>
    {
        @Override
        public AttendeeDetailDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AttendeeDetailDTO a = new AttendeeDetailDTO();

            a.setId(rs.getInt("id"));
            a.setBadgeType(rs.getString("badge_type"));
            a.setBadgeNumber(rs.getString("badge_number"));
            a.setBadgePrePrinted(rs.getBoolean("badge_pre_printed"));
            a.setBadgePrinted(rs.getBoolean("badge_printed"));
            Date birthDate = rs.getDate("birth_date");
            if (birthDate != null) {
                LocalDate localBirthDate = birthDate.toLocalDate();
                a.setBirthDate(localBirthDate);
                LocalDate now = LocalDate.now(timezone);
                a.setAge(ChronoUnit.YEARS.between(localBirthDate, now));
            } else {
                a.setBirthDate(null);
                a.setAge(0L);
            }
            Timestamp checkInTime = rs.getTimestamp("check_in_time");
            if (checkInTime != null) {
                a.setCheckInTime(checkInTime.toInstant().atZone(timezone));
            } else {
                a.setCheckInTime(null);
            }

            a.setCheckedIn(rs.getBoolean("checked_in"));
            a.setCompedBadge(rs.getBoolean("comped_badge"));
            a.setCountry(rs.getString("country"));
            a.setEmail(rs.getString("email"));
            a.setEmergencyContactFullName(rs.getString("emergency_contact_full_name"));
            a.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
            a.setFanName(rs.getString("fan_name"));
            a.setFirstName(rs.getString("first_name"));
            a.setLastName(rs.getString("last_name"));
            a.setLegalFirstName(rs.getString("legal_first_name"));
            a.setLegalLastName(rs.getString("legal_last_name"));
            a.setPreferredPronoun(rs.getString("preferred_pronoun"));
            a.setNameIsLegalName(rs.getBoolean("name_is_legal_name"));
            a.setPaid(rs.getBoolean("paid"));
            a.setPaidAmount(rs.getBigDecimal("paid_amount"));
            a.setParentFormReceived(rs.getBoolean("parent_form_received"));
            a.setParentFullName(rs.getString("parent_full_name"));
            a.setParentIsEmergencyContact(rs.getBoolean("parent_is_emergency_contact"));
            a.setParentPhone(rs.getString("parent_phone"));
            a.setPhoneNumber(rs.getString("phone_number"));
            a.setPreRegistered(rs.getBoolean("pre_registered"));
            a.setZip(rs.getString("zip"));
            a.setOrderId(rs.getInt("order_id"));
            return a;
        }
    }
}