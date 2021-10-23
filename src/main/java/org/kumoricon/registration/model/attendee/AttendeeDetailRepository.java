package org.kumoricon.registration.model.attendee;

import org.kumoricon.registration.exceptions.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Repository
public class AttendeeDetailRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ZoneId timezone;

    public AttendeeDetailRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.timezone = ZoneId.of("America/Los_Angeles");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public AttendeeDetailDTO findByIdAndOrderId(int attendeeId, int orderId) {
        try {
            return jdbcTemplate.queryForObject(
                    "select attendees.*, b.name as badge_type from attendees JOIN badges b on attendees.badge_id = b.id  where attendees.id=:attendeeId and attendees.order_id = :orderId",
                    Map.of("attendeeId", attendeeId, "orderId", orderId), new AttendeeDetailDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Attendee id " + attendeeId + " not found in Order id " + orderId);
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeDetailDTO> findCheckedIn(OffsetDateTime since) {
        try {
            return jdbcTemplate.query("""
                            SELECT attendees.*, b.name as badge_type FROM attendees 
                            JOIN badges b on attendees.badge_id = b.id 
                            WHERE attendees.checked_in is true AND
                                  last_modified >= :since
                            """,
                    Map.of("since", since), new AttendeeDetailDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
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
            a.setBirthDate(rs.getObject("birth_date", LocalDate.class));
            if (a.getBirthDate() != null) {
                LocalDate now = LocalDate.now(timezone);
                a.setAge(ChronoUnit.YEARS.between(a.getBirthDate(), now));
            } else {
                a.setAge(0L);
            }
            a.setCheckInTime(rs.getObject("check_in_time", OffsetDateTime.class));
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
            a.setMembershipRevoked(rs.getBoolean("membership_revoked"));
            a.setAccessibilitySticker(rs.getBoolean("accessibility_sticker"));
            return a;
        }
    }
}