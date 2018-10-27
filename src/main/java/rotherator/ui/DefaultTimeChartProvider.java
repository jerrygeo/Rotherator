package rotherator.ui;

import org.jfree.chart.JFreeChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTimeChartProvider implements TimeChartProvider {

    public static class Entry {
        public final String rothTransferPlan;
        public final String economy;
        public final JFreeChart chart;

        public Entry(String rothTransferPlan, String economy, JFreeChart chart) {
            this.rothTransferPlan = rothTransferPlan;
            this.economy = economy;
            this.chart = chart;
        }
    }

    private Map<String, JFreeChart> charts = new HashMap<>();

    public DefaultTimeChartProvider(List<Entry> entries) {
        entries.forEach(entry -> {
            charts.put(entry.rothTransferPlan.concat(entry.economy), entry.chart);
        });
    }

    @Override
    public JFreeChart getChart(String rothTransferPlan, String economy) {
        return charts.get(rothTransferPlan.concat(economy));
    }
}
