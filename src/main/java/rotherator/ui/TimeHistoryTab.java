package rotherator.ui;

import org.jfree.chart.ChartPanel;
import rotherator.EconomyAllYrs;
import rotherator.RothXfrsAllYrs1Plan;
import rotherator.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TimeHistoryTab extends JPanel implements RecomputeListener {

    private TimeHistoryControlPanel controlPanel;
    private ChartPanel chartPanel = new ChartPanel(null);
    private TimeChartProvider chartProvider;

    public void setChartProvider(TimeChartProvider chartProvider) {
        this.chartProvider = chartProvider;
        chartPanel.setChart(chartProvider.getChart(controlPanel.getRothTransferPlan(), controlPanel.getEconomy()));
    }

    public TimeHistoryTab(List<RothXfrsAllYrs1Plan> transferPlans, List<EconomyAllYrs> economies) {
        setLayout(new BorderLayout());

        controlPanel = new TimeHistoryControlPanel(transferPlans, economies);
        add(controlPanel, BorderLayout.WEST);
        add(chartPanel, BorderLayout.CENTER);

        controlPanel.onScenarioChanged(event -> {
            chartPanel.setChart(chartProvider.getChart(event.rothTransferPlan, event.economy));
        });
    }

    @Override
    public void recomputed(Map<String, Map<String, Simulation>> scenarios) {

    }
}
