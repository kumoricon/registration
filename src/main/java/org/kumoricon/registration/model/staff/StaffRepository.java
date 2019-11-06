package org.kumoricon.registration.model.staff;
import org.kumoricon.registration.model.SqlHelper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StaffRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ZoneId timezone;

    public StaffRepository(JdbcTemplate jdbcTemplate) {
        this.timezone = ZoneId.of("America/Los_Angeles");
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

    /**
     * Returns 50 staff, in order by department, firstname, lastname
     * @param start Offset
     * @return
     */
    @Transactional(readOnly = true)
    public List<Staff> findAllWithPositions(Integer start) {
        final String sql = "SELECT * FROM staff WHERE deleted = FALSE ORDER BY department, first_name, last_name OFFSET ? LIMIT 50";
        try {
            List<Staff> staffList = jdbcTemplate.query(sql, new StaffRowMapper(), start);
            for (Staff s : staffList) {
                s.setPositions(findPositions(s.getId()));
            }
            return staffList;
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }


    @Transactional(readOnly = true)
    public Staff findByUuid(String uuid) {
        final String sql = "select * from staff where uuid = ?";
        try {
            Staff s = jdbcTemplate.queryForObject(sql, new Object[]{uuid}, new StaffRowMapper());
            s.setPositions(findPositions(s.getId()));
            return s;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<String> findPositions(Integer id) {
        final String sql = "select position from staff_positions where id = ?";
        try {
            return jdbcTemplate.queryForList(sql, new Object[] {id}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Integer countByCheckedIn(boolean checkedIn) {
        final String sql = "select count(*) from staff where checked_in = ?";
        return jdbcTemplate.queryForObject(sql, new Object[] {checkedIn}, Integer.class);
    }

    @Transactional
    public void savePositions(Integer id, List<String> positions) {
        final String clearSql = "DELETE FROM staff_positions where id = ?";
        final String insertSql = "INSERT INTO staff_positions (id, position) VALUES (?, ?)";

        jdbcTemplate.update(clearSql, id);
        if (positions != null) {
            for (String position : positions) {
                jdbcTemplate.update(insertSql, id, position);
            }
        }
    }

    @Transactional
    public void save(Staff staff) {
//        SqlParameterSource namedParameters = new MapSqlParameterSource(
//                "id", staff.getId())
//                .addValue("age_category_at_con", staff.getAgeCategoryAtCon())
//                .addValue("badge_image_file_type", staff.getBadgeImageFileType())
//                .addValue("badge_print_count", staff.getBadgePrintCount())
//                .addValue("badge_printed", staff.getBadgePrinted())
//                .addValue("birth_date", staff.getBirthDate())
//                .addValue("checked_in", staff.getCheckedIn())
//                .addValue("checked_in_at", staff.getCheckedInAt())
//                .addValue("deleted", staff.getDeleted())
//                .addValue("department", staff.getDepartment())
//                .addValue("department_color_code", staff.getDepartmentColorCode())
//                .addValue("first_name", staff.getFirstName())
//                .addValue("has_badge_image", staff.getHasBadgeImage())
//                .addValue("last_modified_ms", staff.getLastModifiedMS())
//                .addValue("last_name", staff.getLastName())
//                .addValue("legal_first_name", staff.getLegalFirstName())
//                .addValue("legal_last_name", staff.getLegalLastName())
//                .addValue("shirt_size", staff.getShirtSize())
//                .addValue("suppress_printing_department", staff.getSuppressPrintingDepartment())
//                .addValue("uuid", staff.getUuid())
//                .addValue("information_verified", staff.getInformationVerified())
//                .addValue("picture_saved", staff.getPictureSaved())
//                .addValue("signature_saved", staff.getSignatureSaved());

        if (staff.getId() == null) {
            Integer id = jdbcTemplate.queryForObject("INSERT INTO staff(age_category_at_con, badge_image_file_type, badge_print_count, badge_printed, birth_date, checked_in, checked_in_at, deleted, department, department_color_code, first_name, has_badge_image, last_modified_ms, last_name, legal_first_name, legal_last_name, preferred_pronoun, shirt_size, suppress_printing_department, uuid, information_verified, picture_saved, signature_saved) " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id",
                    new Object[] {staff.getAgeCategoryAtCon(), staff.getBadgeImageFileType(), staff.getBadgePrintCount(),
                    staff.getBadgePrinted(), staff.getBirthDate(), staff.getCheckedIn(), SqlHelper.translate(staff.getCheckedInAt()),
                    staff.getDeleted(), staff.getDepartment(), staff.getDepartmentColorCode(), staff.getFirstName(),
                    staff.getHasBadgeImage(), staff.getLastModifiedMS(), staff.getLastName(), staff.getLegalFirstName(),
                    staff.getLegalLastName(), staff.getPreferredPronoun(), staff.getShirtSize(), staff.getSuppressPrintingDepartment(),
                    staff.getUuid(), staff.getInformationVerified(), staff.getPictureSaved(), staff.getSignatureSaved()}, Integer.class);
            staff.setId(id);
        } else {
            jdbcTemplate.update("UPDATE staff SET age_category_at_con = ?, badge_image_file_type = ?, badge_print_count = ?, badge_printed = ?, birth_date = ?, checked_in = ?, checked_in_at = ?, deleted = ?, department = ?, department_color_code = ?, first_name = ?, has_badge_image = ?, last_modified_ms = ?, last_name = ?, legal_first_name = ?, legal_last_name = ?, preferred_pronoun = ?, shirt_size = ?, suppress_printing_department = ?, uuid = ?, information_verified = ?, picture_saved = ?, signature_saved = ? WHERE id = ?",
                    staff.getAgeCategoryAtCon(), staff.getBadgeImageFileType(), staff.getBadgePrintCount(),
                    staff.getBadgePrinted(), staff.getBirthDate(), staff.getCheckedIn(), SqlHelper.translate(staff.getCheckedInAt()),
                    staff.getDeleted(), staff.getDepartment(), staff.getDepartmentColorCode(), staff.getFirstName(),
                    staff.getHasBadgeImage(), staff.getLastModifiedMS(), staff.getLastName(), staff.getLegalFirstName(),
                    staff.getLegalLastName(), staff.getPreferredPronoun(), staff.getShirtSize(), staff.getSuppressPrintingDepartment(),
                    staff.getUuid(), staff.getInformationVerified(), staff.getPictureSaved(), staff.getSignatureSaved(),
                    staff.getId());
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
            s.setCheckedInAt(SqlHelper.translate(rs.getTimestamp("checked_in_at")));
            s.setDeleted(rs.getBoolean("deleted"));
            s.setDepartment(rs.getString("department"));
            s.setDepartmentColorCode(rs.getString("department_color_code"));
            s.setFirstName(rs.getString("first_name"));
            s.setHasBadgeImage(rs.getBoolean("has_badge_image"));
            s.setLastModifiedMS(rs.getLong("last_modified_ms"));
            s.setLastName(rs.getString("last_name"));
            s.setLegalFirstName(rs.getString("legal_first_name"));
            s.setLegalLastName(rs.getString("legal_last_name"));
            s.setPreferredPronoun(rs.getString("preferred_pronoun"));
            s.setShirtSize(rs.getString("shirt_size"));
            s.setSuppressPrintingDepartment(rs.getBoolean("suppress_printing_department"));
            s.setUuid(rs.getString("uuid"));
            s.setInformationVerified(rs.getBoolean("information_verified"));
            s.setPictureSaved(rs.getBoolean("picture_saved"));
            s.setSignatureSaved(rs.getBoolean("signature_saved"));

            return s;
        }
    }
}