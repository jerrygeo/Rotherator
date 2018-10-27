package rotherator;

// TODO: think of a better name for this

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class InputData {

    private final List<TaxTable> federal;
    private final List<TaxTable> state;
    private final List<IncomeExpense> incomeExpense;
    private final List<EconomyAllYrs> economies;
    private final float targetAllocation;

    public List<TaxTable> getFederal() {
        return federal;
    }

    public List<TaxTable> getState() {
        return state;
    }

    public List<IncomeExpense> getIncomeExpense() {
        return incomeExpense;
    }

    public List<EconomyAllYrs> getEconomies() {
        return economies;
    }

    public List<RothXfrsAllYrs1Plan> getRothXfers() {
        return rothXfers;
    }

    private final List<RothXfrsAllYrs1Plan> rothXfers;
    private final InitialExpenses initialExpenses;


    public InputData(
            List<TaxTable> federal,
            List<TaxTable> state,
            List<IncomeExpense> incomeExpense,
            List<EconomyAllYrs> economies,
            float targetAllocation, InitialExpenses initialExpenses,
            List<RothXfrsAllYrs1Plan> rothXfers) {
        this.federal = Collections.unmodifiableList(federal);
        this.state = state;
        this.incomeExpense = incomeExpense;
        this.economies = economies;
        this.targetAllocation = targetAllocation;
        this.initialExpenses = initialExpenses;
        this.rothXfers = rothXfers;
    }

    public InitialExpenses getInitialExpenses() {
        return initialExpenses;
    }

/*
    public Function<Float, List<Simulation>> computeSimulations() {
        return scaleFactor -> {
          Scenario.computeAllSimulations(
                  scaleFactor,
                  targetAllocation,
                  rothXfers, economies,
                  incomeExpense,
                  initialExpenses);
        };
    }
*/
}
