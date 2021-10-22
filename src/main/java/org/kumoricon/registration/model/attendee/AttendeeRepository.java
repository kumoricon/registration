package org.kumoricon.registration.model.attendee;

import org.kumoricon.registration.exceptions.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class AttendeeRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AttendeeRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Attendee> findAllByOrderId(int orderId) {
        try {
            return jdbcTemplate.query(
                    "select * from attendees where order_id = :orderId order by id desc",
                    Map.of("orderId", orderId), new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<CheckInByBadgeTypeDTO> getCheckInCountsByBadgeType() {
        final String sql = """
                select name, preRegCheckedIn, preRegNotcheckedIn, atConCheckedIn, atConNotcheckedIn from badges
                left outer join
                (select badge_id, count(*) as preRegCheckedIn from attendees where checked_in is true and pre_registered is true group by badge_id) a on a.badge_id = badges.id
                left outer join
                (select badge_id, count(*) as preRegNotcheckedIn from attendees where checked_in is false and pre_registered is true group by badge_id) b on b.badge_id = badges.id
                left outer join
                (select badge_id, count(*) as atConCheckedIn from attendees where checked_in is true and pre_registered is false group by badge_id) c on c.badge_id = badges.id
                left outer join
                (select badge_id, count(*) as atConNotcheckedIn from attendees where checked_in is false and pre_registered is false group by badge_id) d on d.badge_id = badges.id;
                """;
        try {
            return jdbcTemplate.query(sql, new CheckInByBadgeTypeDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<CheckInByHourDTO> findCheckInCountsByHour() {
        String sql = """
                    SELECT date_trunc('hour', check_in_time) at time zone 'utc' as checkInDate, COALESCE(atConCheckedIn.cnt, 0) as AtConCheckedIn, COALESCE(preRegCheckedIn.cnt, 0) as PreRegCheckedIn,
                    COUNT(checked_in) as Total FROM attendees
                    LEFT OUTER JOIN (SELECT date_trunc('hour', check_in_time) as aCheckInDate, COUNT(attendees.checked_in) as cnt
                    FROM attendees  WHERE attendees.checked_in = TRUE
                    AND attendees.pre_registered = TRUE
                    GROUP BY aCheckInDate) as preRegCheckedIn
                    ON date_trunc('hour', check_in_time) = preRegCheckedIn.aCheckInDate
                    LEFT OUTER JOIN (SELECT date_trunc('hour', check_in_time) as aCheckInDate,
                    COUNT(attendees.checked_in) as cnt
                    FROM attendees
                    WHERE attendees.checked_in = TRUE
                    AND attendees.pre_registered = FALSE
                    GROUP BY aCheckInDate) as atConCheckedIn
                    ON date_trunc('hour', check_in_time) = atConCheckedIn.aCheckInDate
                    WHERE checked_in = TRUE
                    GROUP BY date_trunc('hour', check_in_time), checkInDate, atConCheckedIn.cnt, preRegCheckedIn.cnt
                    ORDER BY checkInDate DESC;
                    """;

        try {
            return jdbcTemplate.query(sql, new CheckInByHourDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Integer findWarmBodyCount() {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM (SELECT DISTINCT first_name, last_name, zip, birth_date FROM attendees WHERE checked_in=TRUE) as t",
                    Map.of(),
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    // Total attendee count calculation. From: https://www.kumoricon.org/history
    // Attendance figures for all years are unique, paid (rather than "turnstile")—this means that weekend badges are
    // counted only once, and the count is a number of unique individual attendees who registered in a given year.
    // Attendance figures count paid membership purchases at standard or VIP rates (staff, exhibitors, artists,
    // guests, industry, press, and complimentary badges are not counted). Prior to 2014, multiple single-day badges
    // were double-counted (for example, a person purchases Saturday, then Sunday the next day); for 2014 and after,
    // only one is counted (this is an estimated less than 2% discrepancy).
    @Transactional(readOnly = true)
    public Integer findTotalAttendeeCount() {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM (SELECT DISTINCT first_name, last_name, zip, birth_date FROM attendees WHERE checked_in=TRUE AND paid_amount > 0) as t",
                    Map.of(),
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public Integer count() {
        String sql = "select count(*) from attendees";
        return jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
    }

    @Transactional(readOnly = true)
    public Attendee findById(int id) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from attendees where id=:id",
                    Map.of("id", id), new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Attendee " + id + " not found");
        }
    }

    @Transactional(readOnly = true)
    public Attendee findByIdAndOrderId(int id, int orderId) throws NotFoundException {
        try {
            return jdbcTemplate.queryForObject(
                    "select * from attendees where id=:id and order_id = :orderId",
                    Map.of("id", id, "orderId", orderId), new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Attendee id " + id + " in Order id " + orderId + " not found");
        }
    }


    @Transactional
    public void delete(Integer attendeeId) {
        if (attendeeId != null) {
            jdbcTemplate.update("DELETE FROM attendees WHERE id = :id", Map.of("id", attendeeId));
        }
    }

    /**
     * Saves or updates an Attendee record. Note that some fields are only inserted, NOT updated. Eg, badge number or
     * pre_registered. This is on purpose - badge numbers should not change.
     * @param attendee Attendee to save
     */
    @Transactional
    public void save(Attendee attendee) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(attendee);
        if (attendee.getId() == null) {
            jdbcTemplate.update("""
                            INSERT INTO attendees(badge_id, badge_number, badge_pre_printed, badge_printed,
                            birth_date, check_in_time, checked_in, comped_badge, country, email,
                            emergency_contact_full_name, emergency_contact_phone, fan_name, first_name, last_name,
                            legal_first_name, legal_last_name, name_is_legal_name, preferred_pronoun, paid,
                            paid_amount, parent_form_received, parent_full_name, parent_is_emergency_contact,
                            parent_phone, phone_number, pre_registered, zip, order_id, membership_revoked, accessibility_sticker) VALUES
                            (:badgeId, :badgeNumber, :badgePrePrinted, :badgePrinted,
                             :birthDate, :checkInTime, :checkedIn, :compedBadge, :country, :email,
                             :emergencyContactFullName, :emergencyContactPhone, :fanName, :firstName, :lastName,
                             :legalFirstName, :legalLastName, :nameIsLegalName, :preferredPronoun, :paid,
                             :paidAmount, :parentFormReceived, :parentFullName, :parentIsEmergencyContact,
                             :parentPhone, :phoneNumber, :preRegistered, :zip, :orderId, :membershipRevoked,
                             :accessibilitySticker)
                            """, params);
        } else {
            jdbcTemplate.update("""
                            UPDATE attendees SET badge_id = :badgeId, badge_pre_printed = :badgePrePrinted,
                            badge_printed = :badgePrinted, birth_date = :birthDate, check_in_time = :checkInTime,
                            checked_in=:checkedIn, comped_badge=:compedBadge, country=:country, email=:email,
                            emergency_contact_full_name=:emergencyContactFullName,
                            emergency_contact_phone=:emergencyContactPhone, fan_name=:fanName, first_name=:firstName,
                            last_name=:lastName, legal_first_name=:legalFirstName, legal_last_name=:legalLastName,
                            name_is_legal_name=:nameIsLegalName, preferred_pronoun=:preferredPronoun, paid=:paid,
                            paid_amount=:paidAmount, parent_form_received=:parentFormReceived,
                            parent_full_name=:parentFullName, parent_is_emergency_contact=:parentIsEmergencyContact,
                            parent_phone=:parentPhone, phone_number=:phoneNumber,
                            zip=:zip, order_id=:orderId, membership_revoked=:membershipRevoked,
                            accessibility_sticker=:accessibilitySticker WHERE id = :id
                            """, params);
        }
    }

    @Transactional(readOnly = true)
    public List<Attendee> findAll() {
        try {
            return jdbcTemplate.query(
                    "select * from attendees", new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<AttendeeOrderDTO> findAllAttendeeOrderDTO() {
        try {
            return jdbcTemplate.query(
                    "select attendees.id, attendees.first_name, attendees.last_name, attendees.order_id, orders.order_id as order_number from attendees join orders on attendees.order_id = orders.id",
                    new AttendeeOrderDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    public List<Attendee> findAllByBadgeType(Integer badgeId) {
        try {
            return jdbcTemplate.query("select * from attendees WHERE badge_id = :badgeId ORDER BY last_name, first_name, fan_name",
                    Map.of("badgeId", badgeId),
                    new AttendeeRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    /**
     * Saves or updates multiple Attendee records. Note that some fields are only inserted, NOT updated. Eg, badge
     * number or pre_registered. This is on purpose - badge numbers should not change.
     * @param attendees Attendees to save
     */
    public void saveAll(List<Attendee> attendees) {
        List<Attendee> toInsert = new ArrayList<>();
        List<Attendee> toUpdate = new ArrayList<>();

        for (Attendee a : attendees) {
            if (a.getId() == null) {
                toInsert.add(a);
            } else {
                toUpdate.add(a);
            }
        }

        if (toInsert.size() > 0) {
            SqlParameterSource[] toInsertParams = new SqlParameterSource[toInsert.size()];
            int i = 0;
            for (Attendee a : toInsert) {
                toInsertParams[i] = new BeanPropertySqlParameterSource(a);
                i++;
            }

            jdbcTemplate.batchUpdate("""
                            INSERT INTO attendees(badge_id, badge_number, badge_pre_printed, badge_printed,
                            birth_date, check_in_time, checked_in, comped_badge, country, email,
                            emergency_contact_full_name, emergency_contact_phone, fan_name, first_name, last_name,
                            legal_first_name, legal_last_name, name_is_legal_name, preferred_pronoun, paid,
                            paid_amount, parent_form_received, parent_full_name, parent_is_emergency_contact,
                            parent_phone, phone_number, pre_registered, zip, order_id, membership_revoked,
                            accessibility_sticker) VALUES
                            (:badgeId, :badgeNumber, :badgePrePrinted, :badgePrinted,
                             :birthDate, :checkInTime, :checkedIn, :compedBadge, :country, :email,
                             :emergencyContactFullName, :emergencyContactPhone, :fanName, :firstName, :lastName,
                             :legalFirstName, :legalLastName, :nameIsLegalName, :preferredPronoun, :paid,
                             :paidAmount, :parentFormReceived, :parentFullName, :parentIsEmergencyContact,
                             :parentPhone, :phoneNumber, :preRegistered, :zip, :orderId, :membershipRevoked,
                             :accessibilitySticker)
                            """, toInsertParams);
        }

        if (toUpdate.size() >0) {
            SqlParameterSource[] toUpdateParams = new SqlParameterSource[toUpdate.size()];

            int i = 0;
            for (Attendee a : toUpdate) {
                toUpdateParams[i] = new BeanPropertySqlParameterSource(a);
                i++;
            }
            jdbcTemplate.batchUpdate("""
                            UPDATE attendees SET badge_id = :badgeId, badge_pre_printed = :badgePrePrinted,
                            badge_printed = :badgePrinted, birth_date = :birthDate, check_in_time = :checkInTime,
                            checked_in=:checkedIn, comped_badge=:compedBadge, country=:country, email=:email,
                            emergency_contact_full_name=:emergencyContactFullName,
                            emergency_contact_phone=:emergencyContactPhone, fan_name=:fanName, first_name=:firstName,
                            last_name=:lastName, legal_first_name=:legalFirstName, legal_last_name=:legalLastName,
                            name_is_legal_name=:nameIsLegalName, preferred_pronoun=:preferredPronoun, paid=:paid,
                            paid_amount=:paidAmount, parent_form_received=:parentFormReceived,
                            parent_full_name=:parentFullName, parent_is_emergency_contact=:parentIsEmergencyContact,
                            parent_phone=:parentPhone, phone_number=:phoneNumber,
                            zip=:zip, order_id=:orderId, membership_revoked=:membershipRevoked,
                            accessibility_sticker=:accessibilitySticker WHERE id = :id
                            """, toUpdateParams);
        }
    }

    @Transactional
    public void setParentFormReceived(Integer attendeeId, Integer orderId, Boolean formReceived) {
        final String SQL = """
                UPDATE attendees SET parent_form_received = :parentFormReceived
                WHERE id=:attendeeId AND order_id = :orderId
                """;
        SqlParameterSource params = new MapSqlParameterSource("attendeeId", attendeeId)
                .addValue("orderId", orderId)
                .addValue("parentFormReceived", formReceived);
        int rows = jdbcTemplate.update(SQL, params);
        if (rows != 1) {
            throw new RuntimeException("Attendee not found");
        }
    }

    private static class AttendeeRowMapper implements RowMapper<Attendee>
    {
        @Override
        public Attendee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Attendee a = new Attendee();

            a.setId(rs.getInt("id"));
            a.setBadgeId(rs.getInt("badge_id"));
            a.setBadgeNumber(rs.getString("badge_number"));
            a.setBadgePrePrinted(rs.getBoolean("badge_pre_printed"));
            a.setBadgePrinted(rs.getBoolean("badge_printed"));
            a.setBirthDate(rs.getObject("birth_date", LocalDate.class));
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

    private static class AttendeeOrderDTORowMapper implements RowMapper<AttendeeOrderDTO> {
        @Override
        public AttendeeOrderDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            AttendeeOrderDTO a = new AttendeeOrderDTO();
            a.setAttendeeId(rs.getInt("id"));
            a.setFirstName(rs.getString("first_name"));
            a.setLastName(rs.getString("last_name"));
            a.setOrderId(rs.getInt("order_id"));
            a.setOrderNumber(rs.getString("order_number"));
            return a;
        }
    }

    private static class CheckInByHourDTORowMapper implements RowMapper<CheckInByHourDTO> {
        @Override
        public CheckInByHourDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CheckInByHourDTO(
                    rs.getObject("checkInDate", OffsetDateTime.class),
                    rs.getInt("preregcheckedin"),
                    rs.getInt("atconcheckedin"));
        }
    }

    private static class CheckInByBadgeTypeDTORowMapper implements RowMapper<CheckInByBadgeTypeDTO> {
        @Override
        public CheckInByBadgeTypeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CheckInByBadgeTypeDTO(rs.getString("name"),
                    rs.getInt("preRegCheckedIn"),
                    rs.getInt("preRegNotCheckedIn"),
                    rs.getInt("atConCheckedIn"),
                    rs.getInt("atConNotCheckedIn"));
        }
    }

}