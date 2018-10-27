package rotherator.ui;

import org.jfree.chart.JFreeChart;

public interface SnapshotChartProvider {
    public JFreeChart getChart(String yearSelector, float scaleFactor);
}
