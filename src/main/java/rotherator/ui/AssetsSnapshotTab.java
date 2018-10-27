package rotherator.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import rotherator.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class AssetsSnapshotTab extends JPanel implements RecomputeListener {


    private ChartPanel chartPanel = new ChartPanel(null);
    private AssetsSnapshotControlPanel snapshotControlPanel;
    private SnapshotChartProvider chartProvider;

    private String[] years;
    private boolean afterTax;

    public void setChart(JFreeChart chart) {
        chartPanel.setChart(chart);
    }

    public void setChartProvider(SnapshotChartProvider chartProvider) {
        this.chartProvider = chartProvider;
        chartPanel.setChart(chartProvider.getChart(snapshotControlPanel.getYearSelector(), 1.0f));
    }

    public AssetsSnapshotTab(int firstYear, String[] arrayOfYrs) {
        this(firstYear, arrayOfYrs, false);
    }

    public AssetsSnapshotTab(int firstYear, String[] arrayOfYrs, boolean afterTax) {
        years = arrayOfYrs;
        this.afterTax = afterTax;
        snapshotControlPanel = new AssetsSnapshotControlPanel(firstYear, arrayOfYrs);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        add(snapshotControlPanel, BorderLayout.WEST);

        snapshotControlPanel.onScenarioChanged(event -> {
            chartPanel.setChart(chartProvider.getChart(event.yearSelector, event.expenseScaleFactor));
        });

    }

    @Override
    public void recomputed(Map<String, Map<String, Simulation>> scenarios) {
        ArrayList<DefaultCategoryDataset> snapShotDataSet = new ArrayList<>();

        for(int yearIndex = 0; yearIndex < years.length; yearIndex++) {
            snapShotDataSet.set(yearIndex, new DefaultCategoryDataset());
        }

        scenarios.forEach((economyDesc, simulationMap) -> {
            simulationMap.forEach((rothDesc, simulation) -> {
                for(int yearIndex = 0; yearIndex < years.length; yearIndex++) {
                    String year = years[yearIndex];
//                    total = simulation.getAssetTimeHistory().get(thisSnapShotYrIndx).getTotal()
//                    snapShotDataSet.get(yearIndex).addValue()
                }
            });
        });

    }
}
