package org.kumoricon.registration.settings;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SettingsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    // Settings key names as stored in database
    public static final String TRAINING_MODE = "trainingMode";
    public static final String ENABLE_PRINTING = "enablePrinting";
    public static final String REPORT_PRINTER_NAME = "reportPrinterName";
    public static final String REQUIRE_STAFF_PHOTO = "requireStaffPhoto";
    public static final String DEFAULT_PASSWORD = "defaultPassword";
    public static final String FORCE_PASSWORD_CHANGE = "forcePasswordChange";

    public SettingsRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertSetting(String name, String value) {
        SqlParameterSource params = new MapSqlParameterSource("name", name)
                .addValue("value", value);

        final String SQL = "INSERT INTO settings(name, value) VALUES (:name, :value) " +
                "ON CONFLICT ON CONSTRAINT settings_pkey DO UPDATE set value=:value WHERE settings.name = :name";
        jdbcTemplate.update(SQL, params);
    }

    public void loadSettingsInto(Settings.Builder builder) {
        Map<String, String> results = jdbcTemplate.query("select name, value from settings", (ResultSetExtractor<Map<String, String>>) rs -> {
                    HashMap<String, String> data = new HashMap<>();
                    while (rs.next()) {
                        data.put(rs.getString("name"), rs.getString("value"));
                    }
                    return data;
                });

        if (results == null) return;
        if (results.containsKey(ENABLE_PRINTING)) {
            builder.setEnablePrinting(Boolean.parseBoolean(results.get(ENABLE_PRINTING)));
        }
        if (results.containsKey(REPORT_PRINTER_NAME)) {
            builder.setReportPrinterName(results.getOrDefault(REPORT_PRINTER_NAME, null));
        }
        if (results.containsKey(TRAINING_MODE)) {
            builder.setTrainingMode(Boolean.parseBoolean(results.get(TRAINING_MODE)));
        }
        if (results.containsKey(DEFAULT_PASSWORD)) {
            builder.setDefaultPassword(results.getOrDefault(DEFAULT_PASSWORD, "password"));
        }
        if (results.containsKey(FORCE_PASSWORD_CHANGE)) {
            builder.setForcePasswordChange(Boolean.parseBoolean(results.get(FORCE_PASSWORD_CHANGE)));
        }
        if (results.containsKey(REQUIRE_STAFF_PHOTO)) {
            builder.setRequireStaffPhoto(Boolean.parseBoolean(results.get(REQUIRE_STAFF_PHOTO)));
        }
    }
}
