package rotherator;

import java.util.ArrayList;
import java.util.List;

public class RawIncomeExpense {
    InitialExpenses initialExpenses;
    Assets initialAssets = new Assets();
    List<IncomeExpense> incomeExpense;
    float targetAllocation;
    int numberMedicarePremiums;

    public InitialExpenses getInitialExpenses() {
        return initialExpenses;
    }

    public Assets getInitialAssets() {
        return initialAssets;
    }

    public List<IncomeExpense> getIncomeExpense() {
        return incomeExpense;
    }

    public float getTargetAllocation() {
        return targetAllocation;
    }

    public int getNumberMedicarePremiums() {
        return numberMedicarePremiums;
    }

    public void setNumberMedicarePremiums(int numberMedicarePremiums) {
        this.numberMedicarePremiums = numberMedicarePremiums;
    }

    public RawIncomeExpense(){
        initialExpenses = new InitialExpenses();
        initialAssets = new Assets();
        incomeExpense = new ArrayList<IncomeExpense>();
    }
}
