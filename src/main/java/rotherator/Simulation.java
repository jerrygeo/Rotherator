package rotherator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static rotherator.Main.*;

public class Simulation {
//     This class contains the full simulation for all years of 1 combination of a rothScenario and an economy scenario
    static float fullMedicareCost = 631.0F*12.0F*2.0F; // 4 Times the standard Part B cost, 2 people
    static float medicareBrackets[] = { 0.0F, 170000.0F, 320000.0F, 428000.0F};
    static float medicarePercent[] = { 0.25F, 0.35F, 0.50F, 0.80F};


    int rothXferIndex;  // Allows identifying which RothXfer case (i.e., which csv file) was used
    int economyIndex;   // Allows identifying which economy case (i.e., which csv file) was assumed

//    public static float getFullMedicareCost() {
//        return fullMedicareCost;
//    }
//
//    public static float[] getMedicareBrackets() {
//        return medicareBrackets;
//    }
//
//    public static float[] getMedicarePercent() {
//        return medicarePercent;
//    }

    int getRothXferIndex() {
        return rothXferIndex;
    }

    int getEconomyIndex() {
        return economyIndex;
    }

    List<Assets> getAssetTimeHistory() {
        return assetTimeHistory;
    }

    public Assets getAssets(String year) {
        return assetTimeHistory.stream().filter(a -> a.getYear().equals(year)).findFirst().get();
    }

    public List<IncomeExpense> getExpensesList() {
        return expensesList;
    }

    List<Assets> assetTimeHistory = new ArrayList<Assets>();
    private List<IncomeExpense> expensesList = new ArrayList<IncomeExpense>();


    Simulation(Assets initialAssets, List<IncomeExpense> OriginalIncomeExpenseList, float targetAllocation, int rothIndex, int econIndex) {
        Assets newAsset = new Assets( initialAssets.getYear(), initialAssets.savings, initialAssets.ira, initialAssets.roth,
                initialAssets.savingsAllocation, initialAssets.iraAllocation, initialAssets.rothAllocation, initialAssets.savingsStockBasis);
        this.assetTimeHistory.add(newAsset);
        this.rothXferIndex = rothIndex;
        this.economyIndex = econIndex;
        this.assetTimeHistory.get(0).Allocate(targetAllocation);
        ListIterator litr = OriginalIncomeExpenseList.listIterator();
        while (litr.hasNext()) {
            try {
                Object nextItem = litr.next();
                IncomeExpense newIncomeExpense = new IncomeExpense((IncomeExpense) nextItem);
                expensesList.add(newIncomeExpense);
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                break;
            }

        }


    }

    void computeScenario(float expenseScaleFactor, List<RothXfer1Yr> rothScenario, List<Economy1Yr> econScenario, RawIncomeExpense rawIncomeExpensesList,
                                 PrintWriter rotheratorOut)
            throws IraOverdrawnException, InsufficientFundsException
    {
        int nyears = rothScenario.size();

        //         Set initial allocations to achieve target allocation.
//        System.out.println("Initial allocation: ");
//        System.out.println("Savings,   IRA,   Roth,  SavingsAlloc, IRA_Alloc, Roth_Alloc,  MRD, XtraIRA, CapGain, StockBasis");
//        System.out.println( assetTimeHistory.get(0).toString() );
//        rotheratorOut.println("Initial allocation: ");
//        rotheratorOut.println("Savings,   IRA,   Roth,  SavingsAlloc, IRA_Alloc, Roth_Alloc,  MRD, CapGain, StockBasis");


        String hdr1 = new String("Year,  Total Assets, Total After Tax, Savings,   IRA,   Roth,  SavAlloc, IRA_Alloc, Roth_Alloc,  MRD, XtraIRA, CapGain, StockBasis,");
        String hdr2 = new String("Inflation,  Interest, StockGrowth, Dividends, TaxTbl, ");
        String hdr3 = new String("RothXfer, Age,    SocSec, TaxableIncome, TaxFree,   Inv.Inc.,      AGI,     Taxes, Medicare, Expenses,  Property Tax, Mortgage Int., Other Deductions, Tax Credits, Cumulative Inflation, Tax Table");
        inflationFactor.reset();
        for (int ndxOfYear=0; ndxOfYear < nyears; ndxOfYear++)
        {   // Medicare cost depends on Modified Adjusted Gross Income on tax return from 2 years ago, and we still need to
            // pay taxes from last year.
            if (ndxOfYear == 0)
            {       // Print header
                System.out.print(hdr1);
                System.out.print(hdr2);
                System.out.println(hdr3);
                rotheratorOut.print(hdr1);
                rotheratorOut.print(hdr2);
                rotheratorOut.println(hdr3);
                System.out.println( "Initial: ,,," + assetTimeHistory.get(0).toString() );
                rotheratorOut.println( "Initial: ,,," + assetTimeHistory.get(0).toString() );

                expensesList.get(0).setMedicare(Main.MedicareCost(rawIncomeExpensesList.initialExpenses.getAGI2YearsAgo()));
                expensesList.get(0).setLastYearTaxes(rawIncomeExpensesList.initialExpenses.getTotalTaxLastYear());
            }
            else if (ndxOfYear == 1)
            {
                expensesList.get(1).setMedicare(Main.MedicareCost(rawIncomeExpensesList.initialExpenses.getAGILastYear()));
                expensesList.get(1).setLastYearTaxes(expensesList.get(0).getTaxes());
            }
            else
            {
                expensesList.get(ndxOfYear).setMedicare(Main.MedicareCost(expensesList.get(ndxOfYear -2).getAgi()));
                expensesList.get(ndxOfYear).setLastYearTaxes(expensesList.get(ndxOfYear - 1).getTaxes());

            }
            //  Now we're ready to deal with the current year.....
            computeCurrentYear(expenseScaleFactor, assetTimeHistory.get(ndxOfYear), rothScenario.get(ndxOfYear), econScenario.get(ndxOfYear), expensesList.get(ndxOfYear));
            StringBuffer thisYear = new StringBuffer();
            thisYear.append(String.format("%4d", rothScenario.get(ndxOfYear).year));
            thisYear.append(", ");
            thisYear.append(String.format("%7.0f", assetTimeHistory.get(ndxOfYear).getTotal()));
            thisYear.append(", ");
            thisYear.append(String.format("%7.0f", assetTimeHistory.get(ndxOfYear).getTotalAfterTax()));
            thisYear.append(", ");
            thisYear.append(assetTimeHistory.get(ndxOfYear).toString());
            thisYear.append(", ");
            thisYear.append(econScenario.get(ndxOfYear).toString());
            thisYear.append(",      ");
            thisYear.append(rothScenario.get(ndxOfYear).getRothXferThisYear());
            thisYear.append("   ,");
            thisYear.append(expensesList.get(ndxOfYear).toString());
            thisYear.append("  ,");
            thisYear.append(String.format("%4.2f", inflationFactor.getSinceFirstYear()));
            thisYear.append(", " + Main.federal.get(econScenario.get(ndxOfYear).getTaxTableIndex()).getTableDescription());
            System.out.println(thisYear);
            rotheratorOut.println(thisYear);

            Assets nextAsset = new Assets(assetTimeHistory.get(ndxOfYear));
            if (ndxOfYear < nyears-1) {
                assetTimeHistory.add(nextAsset); // Set up Savings, IRA, Roth for beginning of next year.
                assetTimeHistory.get(assetTimeHistory.size()-1).setCapitalGain(0.F); // Clear out the capital gain field of new record
            }
        }


    }

    private void computeCurrentYear(float scaleFactor, Assets assets, RothXfer1Yr rothXfer, Economy1Yr economy, IncomeExpense incomeExpense)
            throws IraOverdrawnException, InsufficientFundsException
    {
//     Compute items that depend only on current year
//         Need to already have set Medicare cost for year (which depends on income from 2 years ago)
//         and tax computed from previous year to be paid this year.
//
//   It's the beginning of the year. We start the year with a portfolio that has been pulled away from
//             the target allocation due to the expenses having been withdrawn at the end of last year. So, we'll
//             rebalance stock/bond allocation and remember any capital gains resulting from stock sale in the Savings
//             account. We'll rebalance again at the end of the year after the interest/dividends are computed.
        assets.Allocate(rawIncomeExpense.targetAllocation);
//         Miminum Required Distribution (MRD) and extra IRA->Roth conversion.
//             Withdraw this at the beginning of the year since MRD might be needed to  cover expenses during the year.
//             Assume that the allocations in Savings, IRA, and Roth accounts are preserved during this withdrawal.
        float mrd = MinimumRequiredDistribution(incomeExpense.getAge(), assets.getIra());
        assets.setMrd(mrd);
        float xtra = rothXfer.getRothXferThisYear();
        if (assets.getIra() < mrd + xtra) {
            if (assets.getIra() < mrd) {
                System.out.println("Somethings wrong: Min. required IRA distrubition = " + mrd + " but IRA only has " + assets.getIra() + ". Try reducing Roth transfers.");
                System.exit(-1);
            }
            else {  // Trying to transfer too much to Roth - need to reduce transfer.
                xtra = assets.getIra() - mrd;
            }
        }
        assets.setXtraIRA_Withdrawal(xtra);
        assets.setRoth(assets.getRoth() + xtra);
        assets.setIra(assets.getIra() - mrd - xtra);
        if (assets.getIra() < 0.F) {
                throw new IraOverdrawnException(rothXfer.getYear());
        }
//  Grow accounts based on interest, dividends, stock growth rate
        //   Grow Savings, save interest & dividends for tax computation later
        //   Again, we will assume that the funds are reinvested to preserve the allocation
        float interestAndDividends = growInvestments(incomeExpense, assets, economy);
        float savingsCash = interestAndDividends + mrd;
//
//  Now figure up the  social security and tax
        //
        //   Increase social security income by inflation rate
        //
        incomeExpense.setSocialSecurity(incomeExpense.getSocialSecurity()*inflationFactor.sinceFirstYear);
        //
        //   Add non-investment income to savings income
        //
        savingsCash += incomeExpense.getTaxableIncome() + incomeExpense.getTaxFreeIncome() +
                incomeExpense.getSocialSecurity();
        //
        //    Determine "Combined income" to allow calculating tax on Social Security.
        float combinedIncome = incomeExpense.getDividendIncome() + incomeExpense.getInterestIncome() +
                incomeExpense.getTaxableIncome() + assets.getMrd() + assets.getXtraIRA_Withdrawal() +
                .5F* incomeExpense.getSocialSecurity() + assets.getCapitalGain();
        incomeExpense.setCombinedIncome(combinedIncome);
        //
        //   Determine taxes, including taxes on any IRA withdrawal needed last year to cover expenses.
        float taxableSocialSecurity =  computeTaxableSSI(incomeExpense.getSocialSecurity(), true, combinedIncome, inflationFactor.getSinceFirstYear());
        float adjustedGrossIncome = incomeExpense.getDividendIncome() + incomeExpense.getInterestIncome() +
                incomeExpense.getTaxableIncome() + taxableSocialSecurity + assets.getCapitalGain() + assets.getMrd() +
                assets.getXtraIRA_Withdrawal() + assets.getIraWithDrawalToCoverExpenses();
        // Now update extraIRA_Withdrawal to include any extra withdrawn last year for expenses. This allows us
        //     to see the extra withdrawal in Rotherator.csv without adding another column.
        assets.setXtraIRA_Withdrawal(assets.getXtraIRA_Withdrawal() + assets.getIraWithDrawalToCoverExpenses());
        incomeExpense.setAgi(adjustedGrossIncome);
            // Note that tax table number in EconomyX.csv starts at 1, but the array index starts at 0.
            //    We subtract 1 from the tax table index in the constructor for Economy1Yr.
            // For California, capital gain is treated as ordinary income
        float stateTax = Main.state.get(economy.getTaxTableIndex()).computeAnnualTax(adjustedGrossIncome+assets.getCapitalGain(), 0.F, incomeExpense.deductions, 0.0F, inflationFactor.getSinceFirstYear(), incomeExpense.getCredits());
        float fedtax = Main.federal.get(economy.getTaxTableIndex()).computeAnnualTax(adjustedGrossIncome, stateTax, incomeExpense.deductions, assets.getCapitalGain(), inflationFactor.getSinceFirstYear(), incomeExpense.getCredits());
        incomeExpense.setTaxes(stateTax + fedtax);
        //  Tax on capital gain has been figured so clear capital gain. It might be bumped if we need to sell stock to pay expenses.
        //     This will get copied over to the assets for next year.
        assets.setCapitalGain(0.F);
        //
        //    Compute living expenses adjusted for inflation-
        //        Medicare should have been computed (based on income from2 years ago) before calling computeCurrentYear
        //
        incomeExpense.setExpenses(incomeExpense.getExpenses() * scaleFactor * inflationFactor.getSinceFirstYear());
        //
        //    Deduct expenses from savings income, then from assets
        //
        float totalExpenses = incomeExpense.getExpenses() + incomeExpense.getMedicare() + fedtax + stateTax;
        payExpenses(totalExpenses, savingsCash, assets, rothXfer.getYear());
        assets.setTotal( assets.getSavings() + assets.getIra() + assets.getRoth() );
          // Now compute the approximate amount of assets available now after taxes - assume that 15% of stock
        //     profits in Savings and 20% of IRA get gobbled up by taxes.
        float taxOnSavings = (assets.getSavings()*assets.getSavingsAllocation() - assets.getSavingsStockBasis())*.15F;
        float taxOnIRA = assets.getIra()*.25F;
        assets.setTotalAfterTax(assets.getTotal() - taxOnIRA - taxOnSavings);
        Main.inflationFactor.updateForNextYear(economy.getInflationRate());
        economy.setCumulativeInterest(inflationFactor.getSinceFirstYear());
    }

    public class InsufficientFundsException extends Exception
    {
        private float yearFundsExhausted;

        InsufficientFundsException(int yearYouAreBroke)
        {
            super("Funds exhaused before end of simulation.");
            yearFundsExhausted = yearYouAreBroke;
        }

        @Override
        public String toString()
        {
            return ("Year funds exhausted: " + yearFundsExhausted);
        }
    }

    public class IraOverdrawnException extends Exception
    {
        private float yearFundsExhausted;

        IraOverdrawnException(int yearYouAreBroke)
        {
            super("IRA Funds exhaused before end of simulation.");
            yearFundsExhausted = yearYouAreBroke;
        }

        @Override
        public String toString()
        {
            return ("Year funds exhausted: " + yearFundsExhausted);
        }
    }


    public static float computeTaxableSSI(float ssiIncome, boolean marriedFilingJointly, float combinedIncome, float inflationFactor)
    {       // Only a portion of Social Security income is taxed
        // Follow Social Security Benefits worksheet from 2016 Form 1040 - horribly confusing!!
        // Tested using 2016 TurboTax with $40K SSI, and 3 different combinedIncome values.
        float line2 = 0.5f * ssiIncome;
        float line3 = combinedIncome;
        float line7 = line2 + line3;
        float line8 = marriedFilingJointly ? 32000.F : 25000.F;
        float line9 = line7 - line8;
        float taxableAmount = 0.F;
        if (line9 > 0.F) {
            float line10 = marriedFilingJointly ? 12000.F : 9000.F;
            float line11 = line9 - line10;
            if (line11 < 0.F) line11 = 0.F;
            float line12 = (line9 < line10) ? line9 : line10;
            float line13 = line12 * 0.5F;
            float line14 = (line2 < line13) ? line2 : line13;
            float line15 = line11 * 0.85F;
            float line16 = line14 + line15;
            float line17 = ssiIncome * 0.85F;
            taxableAmount = (line16 < line17) ? line16 : line17;
        }
        return taxableAmount;
    }

//  TODO: Generate unit tests for payExpenses and growInvestments


    float growInvestments(IncomeExpense incomeExpense, Assets assets, Economy1Yr economy){
        // growInvestments
        //  Accumulate interest, dividends and stock growth
        //      Returns: sum of interest and dividends in Savings account
        //      Updates assets: Roth, IRA, and SavingsAllocation
        //   First update Savings account.
        //      Interest and dividends will go to cash to be used for expenses, so they don't update Savings now.
        //      Change in stock value will modify the current allocation  (stocks / total )
        //           but does not change the stock basis.
        float savingsBondFraction = (1.0F - assets.getSavingsAllocation());
        float interest = assets.getSavings() * savingsBondFraction * economy.getInterestRate();  // Taxable interest
        incomeExpense.setInterestIncome(interest);
        float dividends = assets.getSavings() * assets.getSavingsAllocation() * economy.getDividendRate() ;
        incomeExpense.setDividendIncome(dividends);
        float interestAndDividends = interest + dividends;
        //              Note that savingsStockGrowth might be negative if the stock market drops
        float savingsStockGrowth = assets.getSavings() * assets.getSavingsAllocation() * economy.getStockGrowthRate();
        if (assets.getSavings() > 0.1F) {  // Avoid dividing by 0
            float newAllocation = ( assets.getSavings() * assets.getSavingsAllocation() + savingsStockGrowth ) / ( assets.getSavings() + savingsStockGrowth);
            assets.setSavingsAllocation(newAllocation);
        }
        assets.setSavings(assets.getSavings() + savingsStockGrowth );
        //   For IRA and Roth, assume that allocation is preserved - we can sell stock with no tax consequences,
        //       and don't need to worry about stock basis for these accounts
        //    Grow IRA
        float iraBondFraction = (1.0F - assets.getIraAllocation());
        float iraGrowth = assets.getIraAllocation()*economy.getStockGrowthRate() + iraBondFraction * economy.getInterestRate();
        assets.setIra( assets.getIra() * ( 1.0F + iraGrowth ) );
//           Grow Roth
        float rothBondFraction = (1.0F - assets.getRothAllocation());
        float rothGrowth = assets.getRothAllocation() * ( economy.getStockGrowthRate() + economy.getDividendRate() ) +
                rothBondFraction * economy.getInterestRate();
        assets.setRoth(assets.getRoth() * ( 1.0F + rothGrowth ));
        return interestAndDividends;
    }

    void payExpenses(float totalExpenses, float savingsCash, Assets assets, int thisYear)
        throws InsufficientFundsException {
        // payExpenses
        //     Use the cash to pay expenses, and withdraw extra funds if necessary, starting with the Savings, then
        //         the IRA, and then the Roth account. Assume stock vs. bond allocations are preserved in IRA and Roth.
        //      If withdrawing from Savings, sell both stocks and bonds to preserve allocation, tracking capital gain and stock basis.
        //      If there's extra cash after paying expenses, invest in bonds with the expectation that we will
        //          reallocate Savings at beginning of next year
        float stockSaleProceeds = 0.F;
        if (savingsCash >= totalExpenses) {  // Income completely covers expenses
            savingsCash -= totalExpenses;
            // Invest cash in bonds for now to avoid changing stock basis. We will reallocate later.
            float newSavingsAlloc = assets.getSavings() * assets.getSavingsAllocation() / (assets.getSavings() + savingsCash);
            assets.setSavingsAllocation(newSavingsAlloc);
            assets.setSavings(assets.getSavings() + savingsCash);
        }
        else { // Income didn't cover expenses - we need to dip into assets
            totalExpenses -= savingsCash;
            if (totalExpenses > assets.getSavings())
            {           // Savings exhausted - clean out Savings and Savings Stock Basis and try to pull any needed additional funds from IRA
                float expensesAferDepletingSavings = totalExpenses - assets.getSavings();
                stockSaleProceeds = assets.getSavings()*assets.getSavingsAllocation();
                assets.setCapitalGain(stockSaleProceeds - assets.getSavingsStockBasis()); // We're selling all the stock
                assets.setSavingsStockBasis(0.F);
                assets.setSavings(0.0F);
                if (expensesAferDepletingSavings > assets.getIra())
                {       // IRA exhausted - try to pull funds from Roth
                    assets.setXtraIRA_Withdrawal(assets.getIra());
                    expensesAferDepletingSavings -= assets.getIra();
                    assets.setIra(0.0F);
                    if (expensesAferDepletingSavings > assets.getRoth())
                    {
                        throw new InsufficientFundsException(thisYear);
                    }
                    else  // Roth not overdrawn - pull out the funds
                    {
                        assets.setRoth(assets.getRoth() - expensesAferDepletingSavings);
                    }
                }
                else
                {     // IRA not exhausted - deduct expenses and remember the extra that we took out (for next year's taxes)
                    assets.setIra(assets.getIra() - expensesAferDepletingSavings);
                    assets.setXtraIRA_Withdrawal(expensesAferDepletingSavings);
                }
            }
            else {  // We can cover the expenses from the Savings account
                // Sell both stocks & bonds to keep the same allocation.
                float totalStockInSavings = assets.getSavings() * assets.getSavingsAllocation();
                if (totalStockInSavings < 1.0F) {  // Avoid divide by 0 below
                    assets.setSavingsStockBasis(0.F);
                }
                else {
                    stockSaleProceeds = totalExpenses * assets.getSavingsAllocation();
                    float fractionOfStockSold = stockSaleProceeds / totalStockInSavings;
                    float longTermCapitalGain = stockSaleProceeds - fractionOfStockSold * assets.getSavingsStockBasis();
                    assets.setCapitalGain(longTermCapitalGain); // Pay tax on this next year
                    assets.setSavingsStockBasis(assets.getSavingsStockBasis() * (1.0F - fractionOfStockSold));
                    if (assets.getSavingsStockBasis() < 0.F) {
                        System.out.println("Error: Something is wrong - basis is negative");
                    }
                }
                assets.setSavings(assets.getSavings() - totalExpenses);
            }
        }

    }
}
