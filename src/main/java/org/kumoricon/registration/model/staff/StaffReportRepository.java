package org.kumoricon.registration.model.staff;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StaffReportRepository {
    private final JdbcTemplate jdbcTemplate;

    public StaffReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<DepartmentReportDTO> findAll() {
        final String sql = "select department, SUM(CASE WHEN checked_in = true THEN 1 ELSE 0 END) as checkedIn,\n" +
                "       SUM(CASE WHEN checked_in = false THEN 1 ELSE 0 END) as notCheckedIn,\n" +
                "       count(id) as total from staff where deleted != true group by department order by department;";
        try {
            return jdbcTemplate.query(sql, new DepartmentReportDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public static class DepartmentReportDTO {
        private final String name;
        private final Integer checkedIn;
        private final Integer notCheckedIn;
        private final Integer total;

        public DepartmentReportDTO(String name, Integer checkedIn, Integer notCheckedIn, Integer total) {
            this.name = name;
            this.checkedIn = checkedIn;
            this.notCheckedIn = notCheckedIn;
            this.total = total;
        }

        public String getName() {
            return name;
        }

        public Integer getCheckedIn() {
            return checkedIn;
        }

        public Integer getNotCheckedIn() {
            return notCheckedIn;
        }

        public Integer getTotal() {
            return total;
        }
    }

    private static class DepartmentReportDTORowMapper implements RowMapper<DepartmentReportDTO> {
        @Override
        public DepartmentReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DepartmentReportDTO(rs.getString("department"),
                    rs.getInt("checkedIn"),
                    rs.getInt("notCheckedIn"),
                    rs.getInt("total"));
        }
    }
}
