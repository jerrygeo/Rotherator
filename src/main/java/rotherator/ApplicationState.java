package rotherator;

import java.util.List;

public class ApplicationState {
    private final List<TaxTable> federal;
    private final List<TaxTable> state;
    private final List<RothXfrsAllYrs1Plan> allRothXferPlans;
    private final RawIncomeExpense rawIncomeExpense;
    private final List<EconomyAllYrs> allEconomies;

    public List<TaxTable> getFederal() {
        return federal;
    }

    public List<TaxTable> getState() {
        return state;
    }

    public List<RothXfrsAllYrs1Plan> getAllRothXferPlans() {
        return allRothXferPlans;
    }

    public RawIncomeExpense getRawIncomeExpense() {
        return rawIncomeExpense;
    }

    public List<EconomyAllYrs> getAllEconomies() {
        return allEconomies;
    }

    public ApplicationState(List<TaxTable> federal, List<TaxTable> state, List<RothXfrsAllYrs1Plan> allRothXferPlans, RawIncomeExpense rawIncomeExpense, List<EconomyAllYrs> allEconomies) {

        this.federal = federal;
        this.state = state;
        this.allRothXferPlans = allRothXferPlans;
        this.rawIncomeExpense = rawIncomeExpense;
        this.allEconomies = allEconomies;
    }
}
