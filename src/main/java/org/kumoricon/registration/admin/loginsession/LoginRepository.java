package org.kumoricon.registration.admin.loginsession;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * I found it easier to just query the database for active sessions instead of trying to read from the Spring
 * Session objects. This also means that it would show all sessions, not just sessions ONLY on this server, which
 * means the list will be more consistent.
 */
@Repository
public class LoginRepository {
    private final JdbcTemplate jdbcTemplate;

    public LoginRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<SessionInfoDTO> findAll() {
        try {
            return jdbcTemplate.query(
                    "select " +
                            "       s.PRIMARY_ID as \"primaryId\", " +
                            "       s.PRINCIPAL_NAME as \"principalName\"," +
                            "       s.CREATION_TIME as \"creationTime\"," +
                            "       s.LAST_ACCESS_TIME as \"lastAccessTime\", " +
                            "       s.EXPIRY_TIME as \"expiryTime\" " +
                            "from SPRING_SESSION s ",
                    new SessionInfoDTORowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private class SessionInfoDTORowMapper implements RowMapper<SessionInfoDTO> {
        @Override
        public SessionInfoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SessionInfoDTO s = new SessionInfoDTO(rs.getString("primaryId"),
                    rs.getString("principalName"),
                    rs.getLong("creationTime"),
                    rs.getLong("lastAccessTime"),
                    rs.getLong("expiryTime"));
            return s;
        }
    }


}
