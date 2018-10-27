package rotherator.ui;

import org.jfree.chart.JFreeChart;
import rotherator.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static rotherator.Main.getListOfViewableYrs;

public class MainWindow extends JFrame {

    private JTabbedPane tabbedPane = new JTabbedPane();

    private TimeHistoryTab timeHistoryTab;
    private AfterTaxTimeTab afterTaxTimeTab;
    private AfterTaxTimeTab preTaxTimeTab;
    private AssetsSnapshotTab assetsSnapshotTab;
    private AssetsSnapshotTab afterTaxSnapshotTab;

    private ApplicationState applicationState;

    private ScaleFactorControlPanel scaleFactorControlPanel;

    private Map<String, Map<String, Simulation>> simulations;

    public void setSnapshotChartProviders(SnapshotChartProvider pretax, SnapshotChartProvider afterTax) {
        assetsSnapshotTab.setChartProvider(pretax);
        afterTaxSnapshotTab.setChartProvider(afterTax);
    }

    public void setTimeHistoryChartProvider(TimeChartProvider timechartProvider) {
        timeHistoryTab.setChartProvider(timechartProvider);
    }

    public void setAfterTaxTimeCharts(JFreeChart[] charts) {
        afterTaxTimeTab.setCharts(charts);

    }

    public void setPreTaxTimeCharts(JFreeChart[] charts) {
        preTaxTimeTab.setCharts(charts);

    }

    //    public void setSnapshotChartProviders(SnapshotChartProvider snapshotChartProvider) {
//        assetsSnapshotTab.setChartProvider(snapshotChartProvider);
//    }
//
//    public void setAfterTaxSnapshotChartProvider(SnapshotChartProvider snapshotChartProvider) {
//        afterTaxSnapshotTab.setChartProvider(snapshotChartProvider);
//    }
    private JFreeChart plotSimulation(Simulation simulation) {
            throw new NotImplementedException();
    }

    public MainWindow(Function<Float, List<Simulation>> computeSimulations) {
        List<Simulation> result = computeSimulations.apply(1.0f);
    }

    public MainWindow(ApplicationState applicationState, PrintWriter rotheratorOut) {

        this.applicationState = applicationState;

        setTitle("ROTHERATOR, v1.00");

        // set up the layout
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        setSize(new Dimension(1600, 900));

        // exit the app when the window is closed
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // add the tabs to the center
        add(tabbedPane, BorderLayout.CENTER);

        // add the scale factor control to the bottom
        scaleFactorControlPanel = new ScaleFactorControlPanel();
        // TODO:
//        scaleFactorControlPanel.addPropertyChangeListener(event -> {
//            simulations = compute((Float) event.getNewValue(), rotheratorOut);
//            for(int tab = 0; tab < tabbedPane.getTabCount(); tab++) {
//                ((RecomputeListener) tabbedPane.getComponentAt(tab)).recomputed(simulations);
//            }
//        });
//        add(scaleFactorControlPanel, BorderLayout.SOUTH);

        List<RothXfrsAllYrs1Plan> transferPlans = applicationState.getAllRothXferPlans();
        List<EconomyAllYrs> economies = applicationState.getAllEconomies();

        // Use the first (0th) plan to get first and last years of simulation - all plans should have same # of years
        int lastYear = transferPlans.get(0).getAllRothXfers1Plan().get(transferPlans.get(0).getAllRothXfers1Plan().size() - 1).getYear();
        int firstYear = transferPlans.get(0).getAllRothXfers1Plan().get(0).getYear();
        // DefaultCategoryDataset timeData = new DefaultCategoryDataset();
        String[] assetSnapShotViewableYrs = getListOfViewableYrs(firstYear,lastYear);

        timeHistoryTab = new TimeHistoryTab(transferPlans, economies);
        afterTaxTimeTab = new AfterTaxTimeTab(economies);
        preTaxTimeTab = new AfterTaxTimeTab(economies);

        assetsSnapshotTab = new AssetsSnapshotTab(firstYear, assetSnapShotViewableYrs);
        afterTaxSnapshotTab = new AssetsSnapshotTab(firstYear, assetSnapShotViewableYrs);

        tabbedPane.addTab("Asset Snapshop", assetsSnapshotTab);
        tabbedPane.addTab("After Tax Snapshop", afterTaxSnapshotTab);
        tabbedPane.addTab("Asset History", timeHistoryTab);
        tabbedPane.addTab("Total After Tax History", afterTaxTimeTab);
        tabbedPane.addTab("Total Pre Tax History", preTaxTimeTab);


        // show the window
        setVisible(true);
    }

    Map<String, Map<String, Simulation>> compute(Float scaleFactor, PrintWriter rotheratorOut) {
        return Scenario.computeAllSimulations1(
                scaleFactor,
                applicationState.getRawIncomeExpense().getTargetAllocation(),
                applicationState.getAllRothXferPlans(),
                applicationState.getAllEconomies(),
                applicationState.getRawIncomeExpense(),
                rotheratorOut);
    }
    
}
