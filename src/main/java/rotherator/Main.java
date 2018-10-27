package rotherator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import rotherator.ui.DefaultSnapshotChartProvider;
import rotherator.ui.DefaultTimeChartProvider;
import rotherator.ui.MainWindow;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static rotherator.Simulation.computeTaxableSSI;


public class Main {

    public static final boolean UNIT_TEST = true;





    static final float medicareBrackets[] = { 0.0F, 170000.0F, 214000.F, 267000.F, 320000.0F};
    static final float medicarePremium[] =  { 134.F, 200.5F,    301.5F,   402.5F, 503.4F};
    public static InflationFactor inflationFactor = new InflationFactor();
    public static SpendingScaleFactor spendingScaleFactor = new SpendingScaleFactor();
//    public static float targetAllocation = 0.0F;

    static final List<TaxTable> federal = rotherator.TaxTable.readTaxCSV("FederalTax.csv");
    static final List<TaxTable> state = rotherator.TaxTable.readTaxCSV("StateTax.csv");
    static final ArrayList<RothXfrsAllYrs1Plan> allRothXferPlans = rotherator.RothXfrsAllYrs1Plan.ReadRothPlansCSV();
    static final RawIncomeExpense rawIncomeExpense = rotherator.IncomeExpense.ReadIncomeExpenseCSV(UNIT_TEST);
    static final ArrayList<EconomyAllYrs> allEconomies = rotherator.EconomyAllYrs.ReadEconomyCSV(UNIT_TEST);





    /*
    public static void closerToIdealMain(String[] args) {
        InputData data = new InputData(
                readTaxCSV("FederalTax.csv"),
                // etc
                // load each thing InputData needs from the respective files
        );

        MainWindow ui = new MainWindow(data.computeSimulations());
    }
*/
    public static AllTheCharts computeEverything(Float scaleFactor) {
        Assets initialAssets = new Assets();
        char[] charCheck = new char[100];  // Used to check if string is digits
        InitialExpenses initialExpenses = new InitialExpenses();

        int indexEconomy;  // Bump this each time a new economyX.csv file is opened
        int indexRothXfer;

        Scenario scenario = new Scenario();
        int maxRothFiles = 0;
        int maxEconomyFiles = 0;
//        List<String> rothDescription = new ArrayList<String>();  // First line of RothXferX.csv
        PrintWriter rotheratorOut = null;

//        if (UNIT_TEST) testMedicare();
//        if (UNIT_TEST) testTax();
//
//        CREATE OUTPUT TEXT FILE rotheratorOut.csv
//
        try {
            rotheratorOut = new PrintWriter("rotheratorOut.csv", "UTF-8");
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
            System.exit(-1);
        }

//
//      NOW PERFORM THE COMPUTATIONS FOR EACH YEAR ON ALL THE COMBINATIONS OF ROTHXFER AND ECONOMY FILES
//
        maxEconomyFiles = allEconomies.size();
        maxRothFiles = allRothXferPlans.size();

        if ((maxEconomyFiles == 0) || (maxRothFiles == 0)) {
            System.out.println("Something's wrong - neither of these should be 0:  maxEconomyFiles=" + maxEconomyFiles + " maxRothFiles=" + maxRothFiles);
            System.exit(-1);
        }

        List<Simulation> simulations = Scenario.computeAllSimulations(scaleFactor, rawIncomeExpense.targetAllocation, allRothXferPlans, allEconomies, rawIncomeExpense, rotheratorOut);

        //   Now print results: remaining available assets at end of simulation and build chart for Final Assets tab
        int scenarioNumber = 0;
        Assets finalAssets = new Assets();
//        for (indexEconomy = 0; indexEconomy < maxEconomyFiles; indexEconomy++) // Print header line
//        {
//            String descriptionOfThisEconomy = allEconomies.get(indexEconomy).description;
//            System.out.print(" ," + descriptionOfThisEconomy);
//            rotheratorOut.print(" ," + descriptionOfThisEconomy);
//        }
//        System.out.println(" ");
//        rotheratorOut.println(" ");
//        ArrayList<ArrayList<Float>> summaryOut = new ArrayList<ArrayList<Float>>();
//        DefaultCategoryDataset chartData = new DefaultCategoryDataset();


        //  Build the datasets and then the charts for Assets Snapshot tab and After Tax Snapshot tab


        ArrayList<DefaultCategoryDataset> snapShotDataSet = new ArrayList<>();
        ArrayList<DefaultCategoryDataset> snapShotMinusTaxDataSet = new ArrayList<>();

        // Use the first (0th) plan to get first and last years of simulation - all plans should have same # of years
        int lastYear = allRothXferPlans.get(0).allRothXfers1Plan.get(allRothXferPlans.get(0).allRothXfers1Plan.size() - 1).getYear();
        int firstYear = allRothXferPlans.get(0).allRothXfers1Plan.get(0).getYear();
        // DefaultCategoryDataset timeData = new DefaultCategoryDataset();
        String[] assetSnapShotViewableYrs = getListOfViewableYrs(firstYear, lastYear); // Make a list of a few years that can be selected for snapshot
        int[] assetSnapShotIndices = new int[assetSnapShotViewableYrs.length];
        int j = 0;
        for (String year : assetSnapShotViewableYrs) {
            assetSnapShotIndices[j++] = (Integer.parseInt(year) );
            snapShotDataSet.add(new DefaultCategoryDataset());
            snapShotMinusTaxDataSet.add(new DefaultCategoryDataset());
        }


        SnapshotSelect snapshotSelect = new SnapshotSelect(false, lastYear);

        // Build the Datasets for Snapshot tabs

        for (indexRothXfer = 0; indexRothXfer < maxRothFiles; indexRothXfer++) {
//            System.out.print(allRothXferPlans.get(indexRothXfer).description);
//            rotheratorOut.print(allRothXferPlans.get(indexRothXfer).description);
            int iCurrentChart = snapShotDataSet.size() - 1;
            for (indexEconomy = 0; indexEconomy < maxEconomyFiles; indexEconomy++) {
                scenarioNumber = indexRothXfer + maxRothFiles * indexEconomy;
                int lastEntry = simulations.get(scenarioNumber).assetTimeHistory.size() - 1; // Index of last year of this simulation
                int finalYear = allRothXferPlans.get(indexRothXfer).allRothXfers1Plan.get(lastEntry).getYear();
                for (int iWhichSnapshot = 0; iWhichSnapshot < assetSnapShotIndices.length; iWhichSnapshot++) {
                    float untaxedAssets;
                    float taxedAssets;
                    int thisSnapShotYrIndx = assetSnapShotIndices[iWhichSnapshot];
                    if (Integer.parseInt(assetSnapShotViewableYrs[iWhichSnapshot]) <= (finalYear-firstYear)) {
                        float cumulativeInflation = allEconomies.get(indexEconomy).allYrsFor1Economy.get(thisSnapShotYrIndx).getCumulativeInflation();
                        untaxedAssets = simulations.get(scenarioNumber).assetTimeHistory.get(thisSnapShotYrIndx).getTotal() / cumulativeInflation;
                        taxedAssets = simulations.get(scenarioNumber).assetTimeHistory.get(thisSnapShotYrIndx).getTotalAfterTax() / cumulativeInflation;
                    } else {
                        untaxedAssets = taxedAssets = 0.F;
                    }
                    snapShotDataSet.get(iWhichSnapshot).addValue(untaxedAssets, allEconomies.get(indexEconomy).description, allRothXferPlans.get(indexRothXfer).description);
                    snapShotMinusTaxDataSet.get(iWhichSnapshot).addValue(taxedAssets, allEconomies.get(indexEconomy).description, allRothXferPlans.get(indexRothXfer).description);
                }

//                finalAssets = simulations.get(scenarioNumber).assetTimeHistory.get(lastEntry); // Assets record of last year of this simulation
//                float availableAssets = finalAssets.getTotalAfterTax();
//                int maxYear = allRothXferPlans.get(indexRothXfer).allRothXfers1Plan.get(allRothXferPlans.get(indexRothXfer).allRothXfers1Plan.size() - 1).getYear();
//                availableAssets /= cumulativeInflation;
//                if (maxYear > finalYear) availableAssets = 0.F;  // Ran out of funds before end of simulation
//                System.out.println(allEconomies.get(indexEconomy).getDescription() + ". " + allRothXferPlans.get(indexRothXfer).getDescription() + ". In " + finalYear + ": $" + availableAssets);
//                System.out.print(", $" + availableAssets);
//                rotheratorOut.print(", $" + availableAssets);

                // add the value to the chart
//                chartData.addValue(availableAssets, allEconomies.get(indexEconomy).description, allRothXferPlans.get(indexRothXfer).description);
            }
//            summaryOut.add(thisEconomy);
//            for (int iEconomy=0; iEconomy<=maxEconomyFiles; iEconomy++) {
//
//            }
//            System.out.println(" ");
//            rotheratorOut.println(" ");

        }

        // Build the charts for Assets snapshot tabs

        ArrayList<DefaultSnapshotChartProvider.Entry> snapshotCharts = new ArrayList<>();
        ArrayList<DefaultSnapshotChartProvider.Entry> afterTaxSnapshotCharts = new ArrayList<>();

        for (int iWhichSnapshot = 0; iWhichSnapshot < assetSnapShotIndices.length; iWhichSnapshot++) {
            String snapshotYear = assetSnapShotViewableYrs[iWhichSnapshot];
            JFreeChart snapshotChart = ChartFactory.createLineChart(
                    String.format("PreTax Assets in %d (in %4d $)", Integer.parseInt(snapshotYear)+firstYear, firstYear),
                    "IRA -> Roth Transfer strategy",
                    "$ (Corrected for Inflation)",
                    snapShotDataSet.get(iWhichSnapshot),
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
            ((LineAndShapeRenderer) snapshotChart.getCategoryPlot().getRenderer()).setBaseShapesVisible(true);
            CategoryAxis axis = snapshotChart.getCategoryPlot().getDomainAxis();
            axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            snapshotChart.getLegend().setPosition(RectangleEdge.RIGHT);
            snapshotCharts.add(new DefaultSnapshotChartProvider.Entry(snapshotYear, snapshotChart));

            JFreeChart afterTaxSnapshotChart = ChartFactory.createLineChart(
                    String.format("After Tax Assets in %d (in %4d $)", Integer.parseInt(snapshotYear)+firstYear, firstYear),
                    "IRA -> Roth Transfer strategy",
                    "$ (Corrected for Inflation)",
                    snapShotMinusTaxDataSet.get(iWhichSnapshot),
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
            ((LineAndShapeRenderer) afterTaxSnapshotChart.getCategoryPlot().getRenderer()).setBaseShapesVisible(true);
            CategoryAxis afterTaxAxis = afterTaxSnapshotChart.getCategoryPlot().getDomainAxis();
            afterTaxAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            afterTaxSnapshotChart.getLegend().setPosition(RectangleEdge.RIGHT);
            afterTaxSnapshotCharts.add(new DefaultSnapshotChartProvider.Entry(snapshotYear, afterTaxSnapshotChart));
        }

        // Build the Time History charts

        List<DefaultTimeChartProvider.Entry> timeCharts = simulations.stream().map(s -> {
            DefaultCategoryDataset timeData = new DefaultCategoryDataset();

            IntStream.range(0, s.assetTimeHistory.size()).forEach(assetIndex -> {
                int year = firstYear + assetIndex;
                Assets th = s.assetTimeHistory.get(assetIndex);
                timeData.addValue(th.getSavings(), "Savings", new Integer(year));
                timeData.addValue(th.getIra(), "IRA", new Integer(year));
                timeData.addValue(th.getRoth(), "Roth", new Integer(year));
//                float totalAssets = th.getSavings() + th.getIra()
//                        + th.getRoth();
                timeData.addValue(th.getTotal(), "Total", new Integer(year));
                timeData.addValue(th.getTotalAfterTax(), "Total-Tax", new Integer(year));
                year = year + 1;
            });

            JFreeChart timeChart = ChartFactory.createLineChart(
                    "Assets over time",
                    "Year",
                    "$ (not scaled down by inflation)",
                    timeData,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
            CategoryAxis assetAxis = timeChart.getCategoryPlot().getDomainAxis();
            assetAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            ((LineAndShapeRenderer) timeChart.getCategoryPlot().getRenderer()).setBaseShapesVisible(true);
            timeChart.getLegend().setPosition(RectangleEdge.RIGHT);
            timeChart.addSubtitle(new TextTitle(allEconomies.get(s.economyIndex).description));
            timeChart.addSubtitle(new TextTitle(allRothXferPlans.get(s.rothXferIndex).description));

            DefaultTimeChartProvider.Entry entry = new DefaultTimeChartProvider.Entry(
                    allRothXferPlans.get(s.rothXferIndex).description,
                    allEconomies.get(s.economyIndex).description,
                    timeChart
            );
            return entry;
        }).collect(Collectors.toList());

//Build the Pre Tax History charts


        // fill pre tax datasets per economy
        DefaultCategoryDataset[] preTaxTimeData = new DefaultCategoryDataset[allEconomies.size()];
        for (int i = 0; i < allEconomies.size(); i++) {
            preTaxTimeData[i] = new DefaultCategoryDataset();
        }

        // for each simulation, put a data point into the dataset corresponding to the simulation's economy index
        for (Simulation simulation : simulations) {
            DefaultCategoryDataset afterTaxTimeDataset = preTaxTimeData[simulation.getEconomyIndex()];
            String rothDescription = allRothXferPlans.get(simulation.getRothXferIndex()).description;
            for (int yearIndex = 0; yearIndex < simulation.getAssetTimeHistory().size(); yearIndex++) {
                Assets assets = simulation.getAssetTimeHistory().get(yearIndex);
                int year = allRothXferPlans.get(simulation.rothXferIndex).getAllRothXfers1Plan().get(0).getYear() + yearIndex;
                float cumulativeInflation = allEconomies.get(simulation.getEconomyIndex()).allYrsFor1Economy.get(yearIndex).getCumulativeInflation();

                afterTaxTimeDataset.addValue(
                        assets.getTotal()/cumulativeInflation,
                        rothDescription,
                        Integer.valueOf(year).toString());
            }
        }

        // build a chart per economy for the pre tax time history tab.
        JFreeChart[] preTaxCharts = new JFreeChart[allEconomies.size()];

        for (int economyIndex = 0; economyIndex < preTaxTimeData.length; economyIndex++) {
            DefaultCategoryDataset dataset = preTaxTimeData[economyIndex];
            JFreeChart preTaxTimeChart = ChartFactory.createLineChart(
                    "Pre-Tax Assets over time",
                    "Year",
                    "$ (scaled down by inflation)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
            CategoryAxis assetAxis = preTaxTimeChart.getCategoryPlot().getDomainAxis();
            assetAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            ((LineAndShapeRenderer) preTaxTimeChart.getCategoryPlot().getRenderer()).setBaseShapesVisible(true);
            preTaxTimeChart.getLegend().setPosition(RectangleEdge.RIGHT);
            preTaxTimeChart.addSubtitle(new TextTitle(allEconomies.get(economyIndex).description));

            preTaxCharts[economyIndex] = preTaxTimeChart;
        }


        //Build the After Tax History charts


        // fill after tax datasets per economy
        DefaultCategoryDataset[] afterTaxTimeData = new DefaultCategoryDataset[allEconomies.size()];
        for (int i = 0; i < allEconomies.size(); i++) {
            afterTaxTimeData[i] = new DefaultCategoryDataset();
        }

        // for each simulation, put a data point into the dataset corresponding to the simulation's economy index
        for (Simulation simulation : simulations) {
            DefaultCategoryDataset afterTaxTimeDataset = afterTaxTimeData[simulation.getEconomyIndex()];
            String rothDescription = allRothXferPlans.get(simulation.getRothXferIndex()).description;
            for (int yearIndex = 0; yearIndex < simulation.getAssetTimeHistory().size(); yearIndex++) {
                Assets assets = simulation.getAssetTimeHistory().get(yearIndex);
                int year = allRothXferPlans.get(simulation.rothXferIndex).getAllRothXfers1Plan().get(0).getYear() + yearIndex;
                float cumulativeInflation = allEconomies.get(simulation.getEconomyIndex()).allYrsFor1Economy.get(yearIndex).getCumulativeInflation();
                afterTaxTimeDataset.addValue(
                        assets.getTotalAfterTax()/cumulativeInflation,
//                        assets.getTotal(),
                        rothDescription,
                        Integer.valueOf(year).toString());
            }
        }

        // build a chart per economy for the after tax time history tab.
        JFreeChart[] afterTaxCharts = new JFreeChart[allEconomies.size()];

        for (int economyIndex = 0; economyIndex < afterTaxTimeData.length; economyIndex++) {
            DefaultCategoryDataset dataset = afterTaxTimeData[economyIndex];
            JFreeChart afterTaxTimeChart = ChartFactory.createLineChart(
                    "After-Tax Assets over time",
                    "Year",
                    "$ (scaled down by inflation)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);
            CategoryAxis assetAxis = afterTaxTimeChart.getCategoryPlot().getDomainAxis();
            assetAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            ((LineAndShapeRenderer) afterTaxTimeChart.getCategoryPlot().getRenderer()).setBaseShapesVisible(true);
            afterTaxTimeChart.getLegend().setPosition(RectangleEdge.RIGHT);
            afterTaxTimeChart.addSubtitle(new TextTitle(allEconomies.get(economyIndex).description));

            afterTaxCharts[economyIndex] = afterTaxTimeChart;
        }

        rotheratorOut.close();

        return new AllTheCharts(timeCharts, afterTaxCharts, preTaxCharts, snapshotCharts, afterTaxSnapshotCharts);
    }

    // TODO: get rid of this class
    public static class AllTheCharts {
        private final List<DefaultTimeChartProvider.Entry> timeCharts;
        private final JFreeChart[] afterTaxTimeCharts;
        private final JFreeChart[] preTaxTimeCharts;
        private final List<DefaultSnapshotChartProvider.Entry> snapshotCharts;
        private final List<DefaultSnapshotChartProvider.Entry> afterTaxSnapshotCharts;

        public AllTheCharts(List<DefaultTimeChartProvider.Entry> timeCharts, JFreeChart[] afterTaxTimeCharts, JFreeChart[] preTaxTimeCharts,List<DefaultSnapshotChartProvider.Entry> snapshotCharts, List<DefaultSnapshotChartProvider.Entry> afterTaxSnapshotCharts) {
            this.timeCharts = timeCharts;
            this.afterTaxTimeCharts = afterTaxTimeCharts;
            this.snapshotCharts = snapshotCharts;
            this.afterTaxSnapshotCharts = afterTaxSnapshotCharts;
            this.preTaxTimeCharts = preTaxTimeCharts;
        }

        public JFreeChart[] getAfterTaxTimeCharts() {
            return afterTaxTimeCharts;
        }

        public List<DefaultTimeChartProvider.Entry> getTimeCharts() {
            return timeCharts;
        }

        public List<DefaultSnapshotChartProvider.Entry> getSnapshotCharts() {
            return snapshotCharts;
        }

        public List<DefaultSnapshotChartProvider.Entry> getAfterTaxSnapshotCharts() {
            return afterTaxSnapshotCharts;
        }
    }

    public static void main(String[] args) {
        
        ApplicationState applicationState = new ApplicationState(
            rotherator.TaxTable.readTaxCSV("FederalTax.csv"),
            rotherator.TaxTable.readTaxCSV("StateTax.csv"),
            rotherator.RothXfrsAllYrs1Plan.ReadRothPlansCSV(),
            rotherator.IncomeExpense.ReadIncomeExpenseCSV(false),
            rotherator.EconomyAllYrs.ReadEconomyCSV(false));

        PrintWriter rotheratorOut = null;

        if (UNIT_TEST) {
            testMedicare();
            testTax();
            testComputeSSITax();
        }

//
//        CREATE OUTPUT TEXT FILE rotheratorOut.csv
//
        try {
            rotheratorOut = new PrintWriter("rotheratorOut.csv", "UTF-8");
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
            System.exit(-1);
        }

        MainWindow window = new MainWindow(applicationState, rotheratorOut);

        AllTheCharts refactorMe = computeEverything(1.0f);

        window.setTimeHistoryChartProvider(
                new DefaultTimeChartProvider(refactorMe.timeCharts));

        window.setAfterTaxTimeCharts(refactorMe.afterTaxTimeCharts);

        window.setPreTaxTimeCharts(refactorMe.preTaxTimeCharts);


        //window.setTimeHistoryChartProvider(new DefaultTimeChartProvider(afterTaxTimeCharts));
        window.setSnapshotChartProviders(
                new DefaultSnapshotChartProvider(scaleFactor -> computeEverything(scaleFactor).snapshotCharts),
                new DefaultSnapshotChartProvider(scaleFactor -> computeEverything(scaleFactor).afterTaxSnapshotCharts));


    }



    public static float MinimumRequiredDistribution(int age, float IRAAmount)
    {       // Minimum requiured IRA distribution
        final float[] yrsRemaining = {
                27.4f,
                26.5f,
                25.6f,
                24.7f,
                23.8f,
                22.9f,
                22f,
                21.2f,
                20.3f,
                19.5f,
                18.7f,
                17.9f,
                17.1f,
                16.3f,
                15.5f,
                14.8f,
                14.1f,
                13.4f,
                12.7f,
                12f,
                11.4f,
                10.8f,
                10.2f,
                9.6f,
                9.1f,
                8.6f,
                8.1f,
                7.6f,
                7.1f,
                6.7f,
                6.3f,
                5.9f,
                5.5f,
                5.2f,
                4.9f,
                4.5f,
                4.2f,
                3.9f,
                3.7f,
                3.4f,
                3.1f,
                2.9f,
                2.6f,
                2.4f,
                2.1f,
                1.9f,
        };
        float amount = 0.0F;
        float timePeriod;
        if (age > 69) {
            if (age < 115) timePeriod = yrsRemaining[age - 70];
            else timePeriod = 1.9f;
            amount = IRAAmount/timePeriod;
        }
        return amount;
    }

    public static String[] getListOfViewableYrs(int firstYr, int lastYr) {
        String[] maxList = new String[5];

        if ( (lastYr - firstYr) < 4 ) {
            System.out.println("Need more years in simulation.");
            System.exit(-1);
        }
        maxList[0] = Integer.toString(3);
        maxList[1] = Integer.toString( (lastYr - firstYr)/4);
        maxList[2] = Integer.toString((lastYr - firstYr)/2);
        maxList[3] = Integer.toString(3*(lastYr - firstYr)/4);
        maxList[4] = Integer.toString(lastYr-firstYr);
        return maxList;
    }

    public static float MedicareCost(float income2YrsAgo)
    {

        int iBracket;
        for ( iBracket=0; iBracket < medicareBrackets.length-2; iBracket++) {
            if (income2YrsAgo < medicareBrackets[iBracket+1] * inflationFactor.sinceFirstYear) break;
        }
        if (income2YrsAgo >= medicareBrackets[medicareBrackets.length-1]) iBracket = medicareBrackets.length-1;
        final float  monthsInAYear = 12.0F;
        return medicarePremium[iBracket] * monthsInAYear * rawIncomeExpense.getNumberMedicarePremiums() * inflationFactor.sinceFirstYear;
    }

    public static void testMedicare() {
        System.out.println("MRD LIFE EXPECTANCY");
        System.out.println("Age  Remaining Years");
        for (int nAge = 69; nAge<120; nAge++)
        {
            float computedDistribution = MinimumRequiredDistribution(nAge, 1.0F);
            float yearsRemaining;
            if (computedDistribution > 0.0F) yearsRemaining = 1.0F/computedDistribution;
            else yearsRemaining = 1000000.F;
            System.out.println(nAge + "    "+ yearsRemaining);
        }
        System.out.println(" ");
        System.out.println("AGI       Medicare Cost");
        final float medicareTestIncome[] = { (medicareBrackets[0]+medicareBrackets[1])/2.0F, medicareBrackets[2], medicareBrackets[3]*1.5F};
        for (int i=0; i < 3; i++) {
            System.out.println(medicareTestIncome[i] + "          " + MedicareCost(medicareTestIncome[i]));
        }
        System.out.println("After inflation of 10%");
        System.out.println("AGI        Medicare Cost");
        inflationFactor.updateForNextYear(0.1F);
        final float medicareTestIncome2[] = { (medicareBrackets[0]+medicareBrackets[1])/2.0F, medicareBrackets[2], medicareBrackets[3]*1.5F};
        for (int i=0; i < 3; i++) {
            System.out.println(medicareTestIncome2[i] + "   " + MedicareCost(medicareTestIncome2[i]));
        }
        inflationFactor.reset();
    }

    final static int YEAR2016 = 10;

    public static void testTax() {
        int lastIndex = federal.get(YEAR2016).brackets.size()-1;
        float testIncome[] = {
                (federal.get(YEAR2016).brackets.get(0).getIncomeBracket() + federal.get(YEAR2016).brackets.get(1).getIncomeBracket())/2,
                federal.get(YEAR2016).brackets.get(3).getIncomeBracket(),
                federal.get(YEAR2016).brackets.get(lastIndex).getIncomeBracket()*1.5F,
                353963.F,
                353963.F,
                150000.F,
                150000.F,
                150000.F,};
        Deductions[] testDeductions = {
                new Deductions(0.F, 0.f, 0.f),
                new Deductions(0.F, 0.f, 0.f),
                new Deductions(0.F, 0.f, 0.f),
                new Deductions(0.F, 0.f, 150000.f),
                new Deductions(0.F, 0.f, 10000.f),
                new Deductions(0.F, 0.f, 1900000.f),
                new Deductions(0.F, 0.f, 10000.f),
                new Deductions(0.F, 0.f, 10000.f),

        };
        Deductions testDeductions1 = new Deductions(0.f, 0.f, 190000.f);
        System.out.println("Test of Taxes (all for year 2016)");
        for (int i=0; i<testIncome.length; i++) {
            float testInflation = 1.F;
            if (i == testIncome.length-1) testInflation = 1.1F;
            if (i == 3) {
                System.out.println("We're here: AGI=" + testIncome[i] + " Deductions=" + testDeductions[i].otherDeductions + " Inflation=" + testInflation );
            }
            float testStateTax = state.get(YEAR2016).computeAnnualTax(testIncome[i], 0.F, testDeductions[i], 0.f,testInflation, 0.F);
            float federalTax = federal.get(YEAR2016).computeAnnualTax(testIncome[i], testStateTax, testDeductions[i], 0.f,testInflation, 0.F);
            System.out.println("AGI=" + testIncome[i] + " Deductions=" + testDeductions[i].otherDeductions + " Inflation=" + testInflation + "  StateTax= " + testStateTax + "  Federal tax= " + federalTax);
        }

    }

    static void testComputeSSITax() {
        System.out.println("Test of SSI Tax Computation");
        boolean marriedFilingJointly = true;
        float testSSI_Income = 40000.F;
        for (int i=0; i<10; i++) {
            float combinedIncome = 5000.F * i;
            float inflationFactor = 1.0F;
            float taxablePortion = computeTaxableSSI(testSSI_Income,  marriedFilingJointly,  combinedIncome,  inflationFactor);
            System.out.println(" SSI Income=" + testSSI_Income + " Combined Income=" + combinedIncome + " Taxable Portion=" + taxablePortion);
        }
    }
}
