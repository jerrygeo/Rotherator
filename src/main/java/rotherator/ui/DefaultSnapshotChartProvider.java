package rotherator.ui;

import org.jfree.chart.JFreeChart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultSnapshotChartProvider implements SnapshotChartProvider{
    public static class Entry {
        public final String snapshotYear;
        public final JFreeChart chart;

        public Entry(String snapshotYear, JFreeChart chart) {
            this.snapshotYear = snapshotYear;
            this.chart = chart;
        }
    }

    // think of Function<Float, List<Entry>> as being just some class called SnapshotChartsBuilder or something

    private Function<Float, List<Entry>> computeEntries;
    private Map<String, JFreeChart> charts;
    private float lastScaleFactor = Float.NaN;

    public DefaultSnapshotChartProvider(Function<Float, List<Entry>> computeEntries) {
        this.computeEntries = computeEntries;
    }

    private void recompute() {
        charts = new HashMap<>();
        computeEntries.apply(lastScaleFactor).forEach(entry -> {
            charts.put(entry.snapshotYear, entry.chart);
        });
    }

    @Override
    public JFreeChart getChart(String snapshotYear, float scaleFactor) {
        if(scaleFactor != lastScaleFactor) {
            lastScaleFactor = scaleFactor;
            recompute();
        }

        return charts.get(snapshotYear);
    }

}
