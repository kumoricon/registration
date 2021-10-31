package org.kumoricon.registration.model.staff;
import org.kumoricon.registration.exceptions.NotFoundException;
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
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class StaffRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StaffRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Staff> findAll() {
        final String sql = "SELECT * FROM staff WHERE deleted = FALSE ORDER BY department, first_name, last_name";
        try {
            return jdbcTemplate.query(sql, new StaffRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Staff> findCheckedIn(OffsetDateTime since) {
        final String sql = """
            SELECT * FROM staff WHERE deleted = FALSE and checked_in is true AND last_modified >= :since
            ORDER BY department, first_name, last_name""";
        try {
            return jdbcTemplate.query(sql, Map.of("since", since), new StaffRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private SqlParameterSource searchStringToQueryTerms(String search) {
        String[] terms = search.trim().split(" ", 2);

        if (terms.length == 1) {
            return new MapSqlParameterSource("first", terms[0]);
        } else {
            return new MapSqlParameterSource("first", terms[0]).addValue("second", terms[1]);
        }
    }

    /**
     * Search for the given name in the staff table. If only a single word is in the search phrase, return
     * records where first OR last or legal first or legal last name start with that (case insensitive)
     * If there is a space in the search phrase, assume that it is "Firstname Lastname" and return records
     * were first_name or legal_first_name starts with firstname, and last_name or legal_last_name starts with
     * the second word.
     * @param search Search phrase
     * @return List of matching records or empty list
     */
    @Transactional(readOnly = true)
    public List<Staff> findByNameLike(String search) {
        if (search == null || search.isBlank()) {
            return new ArrayList<>();
        }
        SqlParameterSource params = searchStringToQueryTerms(search);

        final String singleSQL = "SELECT * FROM staff WHERE deleted = FALSE AND (first_name ILIKE :first||'%' OR last_name ILIKE :first||'%') OR (legal_first_name ILIKE :first||'%' OR legal_last_name ILIKE :first||'%') ORDER BY first_name, last_name";
        final String multiSQL  = "SELECT * FROM staff WHERE deleted = FALSE AND (first_name ILIKE :first||'%' AND last_name ILIKE :second||'%') OR (legal_first_name ILIKE :first||'%' AND legal_last_name ILIKE :second||'%') ORDER BY first_name, last_name";
        try {
            List<Staff> staffList;
            if (params.getParameterNames() != null && params.getParameterNames().length == 1) {
                staffList = jdbcTemplate.query(singleSQL, params, new StaffRowMapper());
            } else {
                staffList = jdbcTemplate.query(multiSQL, params, new StaffRowMapper());
            }
            for (Staff s : staffList) {
                s.setPositions(findPositions(s.getId()));
            }
            return staffList;
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Returns a paged list of staff
     * @param start Offset
     * @return List of staff, in order by lastname, firstname
     */
    @Transactional(readOnly = true)
    public List<Staff> findAllWithPositions(Integer start) {
        final String sql = "SELECT * FROM staff WHERE deleted = FALSE ORDER BY last_name, first_name OFFSET :offset LIMIT 50";
        try {
            List<Staff> staffList = jdbcTemplate.query(sql, Map.of("offset", start), new StaffRowMapper());
            for (Staff s : staffList) {
                s.setPositions(findPositions(s.getId()));
            }
            return staffList;
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    public Staff findByUuid(String uuid) throws NotFoundException {
        final String sql = "select * from staff where uuid = :uuid";
        try {
            Staff s = jdbcTemplate.queryForObject(sql, Map.of("uuid", uuid), new StaffRowMapper());
            if (s != null) {
                s.setPositions(findPositions(s.getId()));
            }
            return s;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Staff uuid " + uuid + " not found");
        }
    }

    @Transactional(readOnly = true)
    public List<String> findPositions(Integer id) {
        final String sql = "select position from staff_positions where id = :id";
        try {
            return jdbcTemplate.queryForList(sql, Map.of("id", id), String.class);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Integer countByCheckedIn(boolean checkedIn) {
        final String sql = "select count(*) from staff where checked_in = :checkedIn";
        return jdbcTemplate.queryForObject(sql, Map.of("checkedIn", checkedIn), Integer.class);
    }

    @Transactional(readOnly = true)
    public Integer count() {
        final String sql = "select count(*) from staff";
        return jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
    }

    @Transactional
    public void savePositions(Integer id, List<String> positions) {
        final String clearSql = "DELETE FROM staff_positions where id = :id";
        final String insertSql = "INSERT INTO staff_positions (id, position) VALUES (:id, :position)";

        jdbcTemplate.update(clearSql, Map.of("id", id));
        if (positions != null && positions.size() > 0) {
            SqlParameterSource[] params = new SqlParameterSource[positions.size()];
            for (int i = 0; i < positions.size(); i++) {
                params[i] = new MapSqlParameterSource("id", id).addValue("position", positions.get(i));
            }
            jdbcTemplate.batchUpdate(insertSql, params);
        }
    }

    @Transactional
    public void save(Staff staff) {
        SqlParameterSource namedParameters = new MapSqlParameterSource(
                "id", staff.getId())
                .addValue("age_category_at_con", staff.getAgeCategoryAtCon())
                .addValue("badge_image_file_type", staff.getBadgeImageFileType())
                .addValue("badge_print_count", staff.getBadgePrintCount())
                .addValue("badge_printed", staff.getBadgePrinted())
                .addValue("birth_date", staff.getBirthDate())
                .addValue("checked_in", staff.getCheckedIn())
                .addValue("checked_in_at", staff.getCheckedInAt())
                .addValue("deleted", staff.getDeleted())
                .addValue("department", staff.getDepartment())
                .addValue("department_color_code", staff.getDepartmentColorCode())
                .addValue("first_name", staff.getFirstName())
                .addValue("has_badge_image", staff.getHasBadgeImage())
                .addValue("last_name", staff.getLastName())
                .addValue("legal_first_name", staff.getLegalFirstName())
                .addValue("legal_last_name", staff.getLegalLastName())
                .addValue("name_privacy_first", staff.getNamePrivacyFirst())
                .addValue("name_privacy_last", staff.getNamePrivacyLast())
                .addValue("phone_number", staff.getPhoneNumber())
                .addValue("preferred_pronoun", staff.getPreferredPronoun())
                .addValue("shirt_size", staff.getShirtSize())
                .addValue("suppress_printing_department", staff.getSuppressPrintingDepartment())
                .addValue("uuid", staff.getUuid())
                .addValue("information_verified", staff.getInformationVerified())
                .addValue("picture_saved", staff.getPictureSaved())
                .addValue("badge_number", staff.getBadgeNumber())
                .addValue("accessibility_sticker", staff.getAccessibilitySticker());

        if (staff.getId() == null) {
            final String SQL = """
                    INSERT INTO staff(age_category_at_con, badge_image_file_type, badge_print_count, 
                      badge_printed, birth_date, checked_in, checked_in_at, deleted, department, department_color_code, 
                      first_name, has_badge_image, last_name, legal_first_name, legal_last_name, 
                      name_privacy_first, name_privacy_last,
                      preferred_pronoun, shirt_size, suppress_printing_department, uuid, information_verified, 
                      picture_saved, badge_number, phone_number, accessibility_sticker, last_modified) 
                    VALUES(:age_category_at_con, :badge_image_file_type, :badge_print_count, :badge_printed,
                      :birth_date, :checked_in,:checked_in_at, :deleted, :department, :department_color_code, 
                      :first_name, :has_badge_image, :last_name, :legal_first_name, :legal_last_name, 
                      :name_privacy_first, :name_privacy_last,
                      :preferred_pronoun, :shirt_size, :suppress_printing_department, :uuid, :information_verified, 
                      :picture_saved, :badge_number, :phone_number, :accessibility_sticker, now()) RETURNING id""";
            Integer id = jdbcTemplate.queryForObject(SQL, namedParameters, Integer.class);
            staff.setId(id);
        } else {
            final String SQL = """
                    UPDATE staff SET age_category_at_con = :age_category_at_con, 
                      badge_image_file_type = :badge_image_file_type, badge_print_count = :badge_print_count, 
                      badge_printed = :badge_printed, birth_date = :birth_date, checked_in = :checked_in, 
                      checked_in_at = :checked_in_at, deleted = :deleted, department = :department, 
                      department_color_code = :department_color_code, first_name = :first_name, 
                      has_badge_image = :has_badge_image,
                      last_name = :last_name, legal_first_name = :legal_first_name, legal_last_name = :legal_last_name, 
                      name_privacy_first = :name_privacy_first, name_privacy_last = :name_privacy_last,
                      preferred_pronoun = :preferred_pronoun, shirt_size = :shirt_size, 
                      suppress_printing_department = :suppress_printing_department, uuid = :uuid, 
                      information_verified = :information_verified, picture_saved = :picture_saved,
                      badge_number = :badge_number, phone_number = :phone_number, 
                      accessibility_sticker = :accessibility_sticker, last_modified = now()
                    WHERE id = :id""";
            jdbcTemplate.update(SQL, namedParameters);
        }
        savePositions(staff.getId(), staff.getPositions());
    }

    private static class StaffRowMapper implements RowMapper<Staff> {
        @Override
        public Staff mapRow(ResultSet rs, int rowNum) throws SQLException {
            Staff s = new Staff();
            s.setId(rs.getInt("id"));
            s.setAgeCategoryAtCon(rs.getString("age_category_at_con"));
            s.setBadgeImageFileType(rs.getString("badge_image_file_type"));
            s.setBadgePrintCount(rs.getInt("badge_print_count"));
            s.setBadgePrinted(rs.getBoolean("badge_printed"));
            Date birthDate = rs.getDate("birth_date");
            if (birthDate != null) {
                s.setBirthDate(birthDate.toLocalDate());
            } else {
                s.setBirthDate(null);
            }
            s.setCheckedIn(rs.getBoolean("checked_in"));
            s.setCheckedInAt(rs.getObject("checked_in_at", OffsetDateTime.class));
            s.setDeleted(rs.getBoolean("deleted"));
            s.setDepartment(rs.getString("department"));
            s.setDepartmentColorCode(rs.getString("department_color_code"));
            s.setFirstName(rs.getString("first_name"));
            s.setHasBadgeImage(rs.getBoolean("has_badge_image"));
            s.setLastName(rs.getString("last_name"));
            s.setLegalFirstName(rs.getString("legal_first_name"));
            s.setLegalLastName(rs.getString("legal_last_name"));
            s.setNamePrivacyFirst(rs.getString("name_privacy_first"));
            s.setNamePrivacyLast(rs.getString("name_privacy_last"));
            s.setPhoneNumber(rs.getString("phone_number"));
            s.setPreferredPronoun(rs.getString("preferred_pronoun"));
            s.setShirtSize(rs.getString("shirt_size"));
            s.setSuppressPrintingDepartment(rs.getBoolean("suppress_printing_department"));
            s.setUuid(rs.getString("uuid"));
            s.setInformationVerified(rs.getBoolean("information_verified"));
            s.setPictureSaved(rs.getBoolean("picture_saved"));
            s.setBadgeNumber(rs.getString("badge_number"));
            s.setAccessibilitySticker(rs.getBoolean("accessibility_sticker"));
            s.setLastModified(rs.getObject("last_modified", OffsetDateTime.class));
            return s;
        }
    }
}