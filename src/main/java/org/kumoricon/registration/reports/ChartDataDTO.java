package org.kumoricon.registration.reports;

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
    private final List<DataSet> data;

    public ChartDataDTO(List<String> labels) {
        this.labels = labels;
        this.data = new ArrayList<>();
    }

    public void addData(DataSet dataSet) {
        this.data.add(dataSet);
    }

    public List<String> getLabels() {
        return labels;
    }

    @JsonProperty("datasets")
    public List<DataSet> getDataSets() {
        return data;
    }

    public static class DataSet {
        private final String label;
        private final String backgroundColor;
        private final String borderColor;
        private final Float borderWidth;
        final List<Integer> data;

        public DataSet(String label, String backgroundColor, String borderColor, Float borderWidth, List<Integer> data) {
            this.label = label;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
            this.data = data;
        }

        public String getLabel() {
            return label;
        }

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public Float getBorderWidth() {
            return borderWidth;
        }

        public List<Integer> getData() {
            return data;
        }
    }
}
