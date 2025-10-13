package org.kumoricon.registration.model.staff;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
public class PositionRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PositionRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Position> findAllByStaff(final Staff staff) {
        final String sql = "select sp.* from staff_position sp left join staff_positions_map spm on spm.staff_id = :id";
        try {
            return jdbcTemplate.query(sql, Map.of("id", staff.getId()), new PositionRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public List<Position> saveAll(final List<Position> positions) {
        final List<Position> toInsert = new ArrayList<>();
        final List<Position> toUpdate = new ArrayList<>();

        positions.forEach(position -> {
            if (position.getId() == null)
                toInsert.add(position);
            else
                toUpdate.add(position);
        });

        return Stream.of(insertPositionsTest(toInsert), updatePositions(toUpdate)).flatMap(Collection::stream).toList();
    }

    private List<Position> insertPositions(final List<Position> positions) {
        if (positions.isEmpty()) return List.of();

        final String sql = """
                    INSERT INTO
                        staff_position(term, department, team, title, position_level, is_elected)
                    VALUES
                        (:term, :department, :team, :title, :positionLevel, :isElected)
                    RETURNING
                        id, term, department, team, title, position_level, is_elected
                """;
        final KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        final SqlParameterSource[] params = positions.stream().map(position -> {
            final SqlParameterSource param = new MapSqlParameterSource()
                    .addValue("term", position.getTerm())
                    .addValue("department", position.getDepartment())
                    .addValue("team", position.getTeam())
                    .addValue("title", position.getTitle())
                    .addValue("positionLevel", position.getLevel())
                    .addValue("isElected", position.isElected());
            return param;
        }).toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, params);

        return positions;
    }

    private List<Position> insertPositionsTest(final List<Position> positions) {
        if (positions.isEmpty()) return List.of();

        final String sql = """
                    INSERT INTO
                        staff_position(term, department, team, title, position_level, is_elected)
                    VALUES
                        :batch
                    RETURNING
                        id, term, department, team, title, position_level, is_elected
                """;
        final List<Object[]> batch = positions.stream()
                .map(position ->
                        new Object[]
                                {position.getTerm(), position.getDepartment(), position.getTeam(), position.getTitle(),
                                        position.getLevel(), position.isElected()})
                .toList();
        return jdbcTemplate.query(sql, new MapSqlParameterSource("batch", batch), new PositionRowMapper());
    }

    private List<Position> updatePositions(final List<Position> positions) {
        if (positions.isEmpty()) return List.of();

        final String sql = """
                    UPDATE
                        staff_position
                    SET
                        term = :term, department = :department, team = :team, title = :title, position_level = :positionLevel,
                        is_elected = :isElected
                    WHERE
                        id = :id
                """;

        final SqlParameterSource[] params = positions.stream().map(position -> {
            final SqlParameterSource param = new MapSqlParameterSource()
                    .addValue("id", position.getId())
                    .addValue("term", position.getTerm())
                    .addValue("department", position.getDepartment())
                    .addValue("team", position.getTeam())
                    .addValue("title", position.getTitle())
                    .addValue("positionLevel", position.getLevel())
                    .addValue("isElected", position.isElected());
            return param;
        }).toArray(SqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, params);

        return positions;
    }

    static class PositionRowMapper implements RowMapper<Position> {
        @Override
        public Position mapRow(ResultSet rs, int rowNum) throws SQLException {
            final Position position = new Position();
            position.setId(rs.getInt("id"));
            position.setTerm(rs.getString("term"));
            position.setDepartment(rs.getString("department"));
            position.setTeam(rs.getString("team"));
            position.setTitle(rs.getString("title"));
            position.setLevel(rs.getString("position_level"));
            position.setIsElected(rs.getBoolean("is_elected"));
            return position;
        }
    }
}
