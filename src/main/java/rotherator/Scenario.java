package rotherator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This class holds the complete set of all simulations.

public class Scenario {


    public static List<Simulation> computeAllSimulations(float scaleFactor,
                                                         float targetAllocation,
                                                         List<RothXfrsAllYrs1Plan> allRothXferPlans,
                                                         List<EconomyAllYrs> allEconomies,
                                                         RawIncomeExpense rawIncomeExpense,
                                                         PrintWriter rotheratorOut) {

        ArrayList<Simulation> listOfSimulations = new ArrayList<Simulation>();
        listOfSimulations.clear();
        for (int indexEconomy = 0; indexEconomy < allEconomies.size(); indexEconomy++) {
            for (int indexRothXfer = 0; indexRothXfer < allRothXferPlans.size(); indexRothXfer++) {
                System.out.println("Starting simulation with, Economy" + indexEconomy + ", Roth" + indexRothXfer);
                rotheratorOut.println("Starting simulation with, Economy" + indexEconomy + ", Roth" + indexRothXfer);
                listOfSimulations.add(new Simulation(rawIncomeExpense.initialAssets,
                        rawIncomeExpense.incomeExpense,
                        targetAllocation,
                        indexRothXfer,
                        indexEconomy));
                try {
                    listOfSimulations.get(listOfSimulations.size() - 1).computeScenario(scaleFactor, allRothXferPlans.get(indexRothXfer).allRothXfers1Plan,
                                                                                        allEconomies.get(indexEconomy).allYrsFor1Economy,
                                                                                    rawIncomeExpense,
                                                                                    rotheratorOut);
//                    scenario.get(scenario.size() - 1).computeScenario(rothXfers.get(2), economies.get(5), incomeExpense, initialExpenses);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
//                System.exit(-1);  // For test
            }
        }

        return listOfSimulations;
    }

    public static Map<String, Map<String, Simulation>> computeAllSimulations1(float expenseScaleFactor,
                                                                              float targetAllocation,
                                                                              List<RothXfrsAllYrs1Plan> allRothXferPlans,
                                                                              List<EconomyAllYrs> allEconomies,
                                                                              RawIncomeExpense rawIncomeExpense,
                                                                              PrintWriter rotheratorOut) {

        Map<String, Map<String, Simulation>> results = new HashMap<>(allEconomies.size());
        for (int indexEconomy = 0; indexEconomy < allEconomies.size(); indexEconomy++) {
            EconomyAllYrs economy = allEconomies.get(indexEconomy);

            for (int indexRothXfer = 0; indexRothXfer < allRothXferPlans.size(); indexRothXfer++) {
                RothXfrsAllYrs1Plan rothXfer = allRothXferPlans.get(indexRothXfer);

                System.out.println("Starting simulation with, Economy" + indexEconomy + ", Roth" + indexRothXfer);
                rotheratorOut.println("Starting simulation with, Economy" + indexEconomy + ", Roth" + indexRothXfer);

                Simulation sim = new Simulation(rawIncomeExpense.initialAssets,
                        rawIncomeExpense.incomeExpense,
                        targetAllocation,
                        indexRothXfer,
                        indexEconomy);
                try {
                    sim.computeScenario(expenseScaleFactor, allRothXferPlans.get(indexRothXfer).allRothXfers1Plan,
                        economy.allYrsFor1Economy,
                        rawIncomeExpense,
                        rotheratorOut);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }

                if(results.get(economy.description) == null) {
                    results.put(economy.description, new HashMap<>(allRothXferPlans.size()));
                }

                results.get(economy.description).put(rothXfer.description, sim);
            }
        }

        return results;
    }

}
