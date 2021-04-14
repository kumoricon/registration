package org.kumoricon.registration.admin.export;


import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportFileControllerTest {

    @Test
    public void blankOrString() {
        Map<String, String[]> tests = Map.of(
                "one word", new String[]{"one word"},
                "test - thing", new String[]{"test", "thing"},
                "test trailing null", new String[]{"test trailing null", null},
                "surrounded by null", new String[]{null, "surrounded by null", null},
                "", new String[]{null});

        for (String expected : tests.keySet()) {
            assertEquals(expected, ExportFileController.blankOrString(tests.get(expected)));
        }
    }
}