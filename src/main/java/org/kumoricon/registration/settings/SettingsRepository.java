package org.kumoricon.registration.settings;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class SettingsRepository {
    private final JdbcTemplate jdbcTemplate;

    // Settings key names as stored in database
    public static final String TRAINING_MODE = "trainingMode";
    public static final String ENABLE_PRINTING = "enablePrinting";
    public static final String REPORT_PRINTER_NAME = "reportPrinterName";
    public static final String REQUIRE_STAFF_PHOTO = "requireStaffPhoto";
    public static final String REQUIRE_STAFF_SIGNATURE= "requireStaffSignature";

    public SettingsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void upsertSetting(String name, String value) {
        final String SQL = "INSERT INTO settings(name, value) VALUES (?, ?) " +
                "ON CONFLICT ON CONSTRAINT settings_pkey DO UPDATE set value=? WHERE settings.name = ?";
        jdbcTemplate.update(SQL, name, value, value, name);
    }

    public void loadSettingsInto(Settings.Builder builder) {
        Map<String, String> results = jdbcTemplate.query("select name, value from settings", (ResultSetExtractor<Map<String, String>>) rs -> {
                    HashMap<String, String> data = new HashMap<>();
                    while (rs.next()) {
                        data.put(rs.getString("name"), rs.getString("value"));
                    }
                    return data;
                });

        if (results.containsKey(ENABLE_PRINTING)) {
            builder.setEnablePrinting(Boolean.parseBoolean(results.get(ENABLE_PRINTING)));
        }
        if (results.containsKey(REPORT_PRINTER_NAME)) {
            builder.setReportPrinterName(results.getOrDefault(REPORT_PRINTER_NAME, null));
        }
        if (results.containsKey(TRAINING_MODE)) {
            builder.setTrainingMode(Boolean.parseBoolean(results.get(TRAINING_MODE)));
        }
    }
}
