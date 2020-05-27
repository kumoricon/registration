package org.kumoricon.registration.model.inlineregistration;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class InLineRegRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InLineRegRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<InLineRegistration> findByRegistrationNumber(String registrationCode) {
        try {
            return jdbcTemplate.query(
                    "select * from inlineregistrations where registration_code = :regCode",
                    Map.of("regCode", registrationCode), new InLineRegRowMapper());
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

    private static class InLineRegRowMapper implements RowMapper<InLineRegistration> {
        @Override
        public InLineRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
            InLineRegistration reg = new InLineRegistration();
            reg.setId(rs.getLong("id"));
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
            reg.setRegistrationNumber(rs.getString("registration_code"));
            reg.setMembershipType(rs.getString("membership_type"));
            return reg;
        }
    }
}
