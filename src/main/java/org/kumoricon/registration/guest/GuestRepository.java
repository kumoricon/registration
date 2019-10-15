package org.kumoricon.registration.guest;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class GuestRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public GuestRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Guest> findAll() {
        return jdbcTemplate.query("select * from guests",
                new GuestRowMapper());
    }

    @Transactional(readOnly = true)
    public Guest findById(int id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", id);
        return jdbcTemplate.queryForObject("select * from guests where id=:id",
                namedParameters, new GuestRowMapper());
    }


    @Transactional(readOnly = true)
    public Guest findByOnlineId(String onlineId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("onlineId", onlineId);
        return jdbcTemplate.queryForObject("select * from guests where online_id=:onlineId",
                namedParameters, new GuestRowMapper());
    }


    @Transactional
    public void delete(Guest guest) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("id", guest.getId());
        if (guest.getId() != null) {
            jdbcTemplate.update("DELETE FROM guests WHERE id = :id", namedParameters);
        }
    }

    @Transactional
    public void save(Guest guest) {
        SqlParameterSource namedParameters = new MapSqlParameterSource(
                "id", guest.getId())
                .addValue("online_id", guest.getOnlineId())
                .addValue("first_name", guest.getFirstName())
                .addValue("last_name", guest.getLastName())
                .addValue("legal_first_name", guest.getLegalFirstName())
                .addValue("legal_last_name", guest.getLegalLastName())
                .addValue("preferred_pronoun", guest.getPreferredPronoun())
                .addValue("fan_name", guest.getFanName())
                .addValue("birth_date", guest.getBirthDate())
                .addValue("age_category_at_con", guest.getAgeCategoryAtCon())
                .addValue("has_badge_image", guest.getHasBadgeImage())
                .addValue("badge_image_file_type", guest.getBadgeImageFileType());

        if (guest.getId() == null) {
            jdbcTemplate.update("INSERT INTO guests(online_id, first_name, last_name, legal_first_name, " +
                            "legal_last_name, preferred_pronoun, fan_name, birth_date, age_category_at_con, has_badge_image, badge_image_file_type) " +
                            "VALUES(:online_id, :first_name, :last_name, :legal_first_name, :legal_last_name, :preferred_pronoun, " +
                            ":fan_name, :birth_date, :age_category_at_con, :has_badge_image, :badge_image_file_type)",
                    namedParameters);
        } else {
            jdbcTemplate.update("UPDATE guests SET online_id = :online_id, first_name = :first_name, " +
                            "last_name = :last_name, legal_first_name = :legal_first_name, legal_last_name = :legal_last_name, " +
                            "preferred_pronoun = :preferred_pronoun, fan_name = :fan_name, birth_date = :birth_date, " +
                            "age_category_at_con = :age_category_at_con, has_badge_image = :has_badge_image, " +
                            "badge_image_file_type = :badge_image_file_type WHERE id = :id",
                    namedParameters);
        }
    }

    @Transactional(readOnly = true)
    public Integer count() {
        SqlParameterSource parameters = new MapSqlParameterSource();
        String sql = "select count(*) from guests";
        return jdbcTemplate.queryForObject(sql, parameters, Integer.class);
    }

    @Transactional(readOnly = true)
    public List<Guest> findAllBy(Integer page) {
        SqlParameterSource parameters = new MapSqlParameterSource("page", page)
                .addValue("pageSize", 20);
        try {
            return jdbcTemplate.query("select * from guests ORDER BY first_name, last_name desc LIMIT :pageSize OFFSET :page*:pageSize",
                    parameters,
                    new GuestRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }



    class GuestRowMapper implements RowMapper<Guest>
    {
        @Override
        public Guest mapRow(ResultSet rs, int rowNum) throws SQLException {
            Guest g = new Guest();
            g.setId(rs.getInt("id"));
            g.setOnlineId(rs.getString("online_id"));
            g.setFirstName(rs.getString("first_name"));
            g.setLastName(rs.getString("last_name"));
            g.setLegalFirstName(rs.getString("legal_first_name"));
            g.setLegalLastName(rs.getString("legal_last_name"));
            g.setPreferredPronoun(rs.getString("preferred_pronoun"));
            g.setFanName(rs.getString("fan_name"));
            Date birthDate = rs.getDate("birth_date");
            if (birthDate != null) {
                g.setBirthDate(birthDate.toLocalDate());
            } else {
                g.setBirthDate(null);
            }
            g.setAgeCategoryAtCon(rs.getString("age_category_at_con"));
            g.setHasBadgeImage(rs.getBoolean("has_badge_image"));
            g.setBadgeImageFileType(rs.getString("badge_image_file_type"));

            return g;
        }
    }
}
