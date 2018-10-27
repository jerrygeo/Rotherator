package rotherator.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import rotherator.EconomyAllYrs;
import rotherator.Simulation;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class AfterTaxTimeTab extends JPanel implements RecomputeListener {

    private AfterTaxTimeControlPanel controlPanel;
    private ChartPanel chartPanel = new ChartPanel(null);
    private JFreeChart[] charts;

    public AfterTaxTimeTab(List<EconomyAllYrs> economies) {
        setLayout(new BorderLayout());


        controlPanel = new AfterTaxTimeControlPanel(economies, this); // terrible design - do not do this in real life!
        add(controlPanel, BorderLayout.WEST);
        add(chartPanel, BorderLayout.CENTER);

    }

    public void setCharts(JFreeChart[] charts) {
        this.charts = charts;
        setEconomyIndex(0);
    }

    public void setEconomyIndex(int index) {
        chartPanel.setChart(charts[index]);
    }

    @Override
    public void recomputed(Map<String, Map<String, Simulation>> scenarios) {

    }
}
