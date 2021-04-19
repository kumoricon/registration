package org.kumoricon.registration.reports.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents chart data used by the Chart.js framework
 *
 * Example JSON that would be used in a stacked bar chart:
 *   {
 *     "labels": ["Chair","Infrastructure","Other"],
 *     "datasets": [
 *       {"label":"Checked In","backgroundColor":"#9999FF","borderColor":"#000000","borderWidth":1.0,"data":[5,14,0]},
 *       {"label":"Not Checked In","backgroundColor":"#776666","borderColor":"#000000","borderWidth":1.0,"data":[1,3,8]}
 *     ]
 *   }
 *
 * Would draw the chart:
 *
 *             Chair: OOOOOo
 *    Infrastructure: OOOOOOOOOOOOOOooo
 *             Other: oooooooo
 */
public class ChartDataDTO {
    private final List<String> labels;
    private final List<ChartDataSetDTO> data;

    public ChartDataDTO(List<String> labels) {
        this.labels = labels;
        this.data = new ArrayList<>();
    }

    public void addData(ChartDataSetDTO dataSet) {
        this.data.add(dataSet);
    }

    public List<String> getLabels() {
        return labels;
    }

    @JsonProperty("datasets")
    public List<ChartDataSetDTO> getDataSets() {
        return data;
    }

}
