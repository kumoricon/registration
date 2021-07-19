package org.kumoricon.registration.model.inlineregistration;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class InLineRegRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InLineRegRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<InLineRegistration> findByConfirmationCode(String confirmationCode) {
        try {
            return jdbcTemplate.query(
                    "select * from inlineregistrations where confirmation_code = :regCode",
                    Map.of("regCode", confirmationCode), new InLineRegRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<InLineRegistration> findByOrderUuid(UUID orderUuid) {
        try {
            return jdbcTemplate.query(
                    "select * from inlineregistrations where order_uuid = :orderUuid",
                    Map.of("orderUuid", orderUuid), new InLineRegRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<InLineRegistration> findByNameLike(String name) {
        final String SQL = "select * from inlineregistrations where first_name ILIKE :term0 || '%' OR " +
                "last_name ILIKE :term0";
        try {
            return jdbcTemplate.query(SQL, Map.of("term0", name), new InLineRegRowMapper());
        } catch (
                EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public void upsert(InLineRegistration inLineRegistration) {
        SqlParameterSource params = new BeanPropertySqlParameterSource(inLineRegistration);
        final String SQL = """
                INSERT INTO inlineregistrations (uuid, order_uuid, first_name, last_name, legal_first_name, legal_last_name,
                name_is_legal_name, preferred_pronoun, zip, country, phone_number, email, birth_date, emergency_contact_fullname,
                emergency_contact_phone, parent_is_emergency_contact, parent_fullname, parent_phone, confirmation_code, membership_type)  VALUES
                (:uuid, :orderUuid, :firstName, :lastName, :legalFirstName, :legalLastName, :nameIsLegalName, :preferredPronoun, :zip,
                :country, :phoneNumber, :email, :birthDate, :emergencyContactFullName, :emergencyContactPhone,
                :parentIsEmergencyContact, :parentFullName, :parentPhone, :confirmationCode, :membershipType) ON CONFLICT (uuid)
                DO UPDATE SET first_name=:firstName, last_name=:lastName, legal_first_name=:legalFirstName, legal_last_name=:legalLastName,
                name_is_legal_name=:nameIsLegalName, preferred_pronoun=:preferredPronoun, zip=:zip, country=:country, phone_number=:phoneNumber, email=:email, birth_date=:birthDate,
                emergency_contact_fullname=:emergencyContactFullName, emergency_contact_phone=:emergencyContactPhone,
                parent_is_emergency_contact=:parentIsEmergencyContact, parent_fullname=:parentFullName, parent_phone=:parentPhone,
                confirmation_code=:confirmationCode, order_uuid=:orderUuid, membership_type=:membershipType WHERE inlineregistrations.uuid=:uuid""";
        jdbcTemplate.update(SQL, params);
    }

    private static class InLineRegRowMapper implements RowMapper<InLineRegistration> {
        @Override
        public InLineRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
            InLineRegistration reg = new InLineRegistration();
            reg.setId(rs.getLong("id"));
            reg.setUuid(UUID.fromString(rs.getString("uuid")));
            reg.setOrderUuid(UUID.fromString(rs.getString("order_uuid")));
            reg.setFirstName(rs.getString("first_name"));
            reg.setLastName(rs.getString("last_name"));
            reg.setLegalFirstName(rs.getString("legal_first_name"));
            reg.setLegalLastName(rs.getString("legal_last_name"));
            reg.setNameIsLegalName(rs.getBoolean("name_is_legal_name"));
            reg.setPreferredPronoun(rs.getString("preferred_pronoun"));
            reg.setZip(rs.getString("zip"));
            reg.setCountry(rs.getString("country"));
            reg.setPhoneNumber(rs.getString("phone_number"));
            reg.setEmail(rs.getString("email"));
            reg.setBirthDate(rs.getObject("birth_date", LocalDate.class));
            reg.setEmergencyContactFullName(rs.getString("emergency_contact_fullname"));
            reg.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
            reg.setParentFullName(rs.getString("parent_fullname"));
            reg.setParentPhone(rs.getString("parent_phone"));
            reg.setParentIsEmergencyContact(rs.getBoolean("parent_is_emergency_contact"));
            reg.setConfirmationCode(rs.getString("confirmation_code"));
            reg.setMembershipType(rs.getString("membership_type"));
            return reg;
        }
    }
}
