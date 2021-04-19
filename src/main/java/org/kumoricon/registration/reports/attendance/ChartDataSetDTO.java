package org.kumoricon.registration.reports.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChartDataSetDTO(
        @JsonProperty String label,
        @JsonProperty String backgroundColor,
        @JsonProperty String borderColor,
        @JsonProperty Float borderWidth,
        @JsonProperty List<Integer> data) {
}

