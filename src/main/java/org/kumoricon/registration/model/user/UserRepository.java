package org.kumoricon.registration.model.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Service
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly=true)
    public User findOneByUsernameIgnoreCase(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT users.*, roles.name as rolename from users join roles on users.role_id = roles.id WHERE users.username=?",
                    new Object[]{username}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional(readOnly=true)
    User findOneById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT users.*, roles.name as rolename from users join roles on users.role_id = roles.id where users.id=?",
                    new Object[]{id}, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Transactional
    public void save(User user) {
        if (user.getId() == null) {
            jdbcTemplate.update("INSERT INTO users " +
                            "(account_non_expired, account_non_locked, credentials_non_expired, enabled, first_name, last_name, last_badge_number_created, password, username, role_id) " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?)",
                    user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getEnabled(), user.getFirstName(), user.getLastName(), user.getLastBadgeNumberCreated(), user.getPassword(), user.getUsername(), user.getRoleId());
        } else {
            jdbcTemplate.update("UPDATE users SET account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ?, enabled = ?, first_name = ?, last_name = ?, last_badge_number_created = ?, password = ?, username = ?, role_id = ? WHERE id = ?",
                    user.getAccountNonExpired(), user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getEnabled(), user.getFirstName(), user.getLastName(), user.getLastBadgeNumberCreated(), user.getPassword(), user.getUsername(), user.getRoleId(), user.getId());
        }
    }

    @Transactional(readOnly=true)
    public List<User> findAll() {
        return jdbcTemplate.query("SELECT users.*, roles.name as rolename from users join roles on users.role_id = roles.id;",
                new UserRowMapper());
    }

    public Integer count() {
        String sql = "select count(*) from users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    class UserRowMapper implements RowMapper<User>
    {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setLastBadgeNumberCreated(rs.getInt("last_badge_number_created"));
            user.setRoleId(rs.getInt("role_id"));
            user.setRoleName(rs.getString("rolename"));
            return user;
        }
    }
}
