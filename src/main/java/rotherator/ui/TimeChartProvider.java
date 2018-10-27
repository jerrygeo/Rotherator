package rotherator.ui;

import org.jfree.chart.JFreeChart;

public interface TimeChartProvider {
    public JFreeChart getChart(String rothTransferPlan, String economy);
}
