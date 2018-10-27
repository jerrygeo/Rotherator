package rotherator;

public class Assets {
    float total;  // Sum of savings + ira + roth
    float totalAfterTax;  // total - 15% of stock profit - 18% IRA
    float savings;
    float ira;
    float roth;
    float mrd; // Minimum required IRA distribution
    float xtraIRA_Withdrawal;
    float iraWithDrawalToCoverExpenses;  // Expenses subtracted at end of year, but if IRA withdrawal needed in order
                                        //   to cover expenses, compute the tax on this additional withdrawal next year.
    float savingsAllocation;  // Percentage of savings in stock funds (vs bond funds)
    float rothAllocation;     // Percentage of Roth account in stock funds (vs bond funds)
    float iraAllocation;
    float savingsStockBasis;
    float capitalGain;      // Long term capital gain this year - computed during simulation

    public String getYear() {
        return year;
    }

    String year;

    public Assets(){
        year = "";
        savings = 0.F;
        ira = 0.F;
        roth = 0.F;
        mrd = 0.F;
        xtraIRA_Withdrawal = 0.F;
        iraWithDrawalToCoverExpenses = 0.F;
        savingsAllocation = 0.0F;
        rothAllocation = 0.0F;
        iraAllocation = 0.0F;
        savingsStockBasis = 0.0F;
        capitalGain = 0.0F;
    }
    public Assets(String year, float argsav, float argira, float argroth, float argsavalloc, float argiraalloc, float argrothalloc, float argStockBasis)
    {
        savings = argsav;
        ira = argira;
        roth = argroth;
        savingsAllocation = argsavalloc;
        rothAllocation = argrothalloc;
        iraAllocation = argiraalloc;
        iraWithDrawalToCoverExpenses = 0.F;
        this.year = year;
        mrd = 0.F;
        xtraIRA_Withdrawal = 0.F;
        savingsStockBasis = argStockBasis;
        capitalGain = 0.0F;

    }

    public Assets(Assets oldAsset) {
        year = ""; //TODO: //Integer.toString(Integer.parseInt(oldAsset.year) + 1);
        savings = oldAsset.getSavings();
        ira = oldAsset.getIra();
        roth = oldAsset.getRoth();
        savingsAllocation = oldAsset.getSavingsAllocation();
        rothAllocation = oldAsset.getRothAllocation();
        savingsStockBasis = oldAsset.getSavingsStockBasis();
        iraWithDrawalToCoverExpenses = oldAsset.getIraWithDrawalToCoverExpenses();
        capitalGain = oldAsset.getCapitalGain();
    }

    public void Allocate(float targetAllocation)
    {  // Re-balance the Savings, IRA, and Roth accounts to achieve the overall desired target allocation, while
        //   ensuring that:
        //       IRA contains bonds to the greatest extent possible, since stocks should be in taxable Savings account
        //           to take advantage of long term capital gains treatment
        //       Roth should contain stock funds to the greatest extent possible, since the profit from
        //       stock growth + dividends is assumed to be higher than bonds, and this larger profit will never be
        //       taxed in the Roth account.
        // If stock is sold in the Savings account to achieve the re-balancing, the profit is computed and added to
        //     capitalGain and the savingsStockBasis is reduced
        float totalAssets = getSavings() + getIra() + getRoth();
        float bondAmount = ( 1.0F - targetAllocation) * totalAssets;
        float preAllocationStockInSavings = getSavings()*getSavingsAllocation();
        if (getIra() >= bondAmount)   // IRA too big to be 100% bonds - must include stocks. Other accounts will be entirely stock funds.
        {
            float newIraAlloc = (getIra() - bondAmount)/getIra();
            setIraAllocation( (getIra() - bondAmount)/getIra());
            setSavingsAllocation(1.0F);
            setRothAllocation(1.0F);
        }
        else     // IRA is entirely bonds - allocate stocks between Savings and Roth
        {
            setIraAllocation(0.0F);
            float remainingBonds = bondAmount - getIra();
            if (remainingBonds > getSavings())
            {                 // Savings is all bonds. Need to put some bonds into Roth
                setSavingsAllocation(0.0F);
                float testAllocation = targetAllocation*totalAssets / getRoth();
                if (testAllocation > 1.0F)
                {
                    System.out.println("Trying to allocate Roth > 1.0: Savings=" + savings + " Roth=" + roth + " IRA =" + ira);
                    System.exit(-1);
                }
                setRothAllocation(testAllocation);
            }
            else
            {             // Savings is partially stocks. Roth is all stocks
                setSavingsAllocation(( getSavings() - remainingBonds ) / getSavings());
                setRothAllocation(1.0F);
            }
        }
        float postAllocationStockInSavings = getSavings()*getSavingsAllocation();
        if (preAllocationStockInSavings > postAllocationStockInSavings) {  // Had to sell stock in Savings to re-balance
            float amountOfStockSold =  preAllocationStockInSavings - postAllocationStockInSavings;
            float fractionOfStockSold = amountOfStockSold / preAllocationStockInSavings;
            float basisOfStockSold = fractionOfStockSold * (preAllocationStockInSavings - getSavingsStockBasis());
            float longTermCapitalGain = amountOfStockSold - basisOfStockSold;
            setCapitalGain(getCapitalGain() + longTermCapitalGain);
            setSavingsStockBasis(getSavingsStockBasis()*(1.0F - fractionOfStockSold));
        }
//        float allocationResult = (getSavings()*getSavingsAllocation() + getIra()*getIraAllocation() + getRoth()*getRothAllocation() )/totalAssets;
//        System.out.println("After allocation % stocks:" + allocationResult );
    }

    public float getIraWithDrawalToCoverExpenses() {
        return iraWithDrawalToCoverExpenses;
    }

    public void setIraWithDrawalToCoverExpenses(float iraWithDrawalToCoverExpenses) {
        this.iraWithDrawalToCoverExpenses = iraWithDrawalToCoverExpenses;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTotalAfterTax() {
        return totalAfterTax;
    }

    public void setTotalAfterTax(float totalAfterTax) {
        this.totalAfterTax = totalAfterTax;
    }

    public float getCapitalGain() {
        return capitalGain;
    }

    public void setCapitalGain(float capitalGain) {
        this.capitalGain = capitalGain;
    }

    public float getSavingsStockBasis() {
        return savingsStockBasis;
    }

    public void setSavingsStockBasis(float savingsStockBasis) {
        this.savingsStockBasis = savingsStockBasis;
    }

    public float getMrd() {
        return mrd;
    }

    public void setMrd(float mrd) {
        this.mrd = mrd;
    }

    public float getXtraIRA_Withdrawal() {
        return xtraIRA_Withdrawal;
    }

    public void setXtraIRA_Withdrawal(float xtraIRA_Withdrawal) {
        this.xtraIRA_Withdrawal = xtraIRA_Withdrawal;
    }

    public float getIraAllocation() {
        return iraAllocation;
    }

    public void setIraAllocation(float newIraAllocation) {
        this.iraAllocation = newIraAllocation;
    }
    public float getSavingsAllocation() {
        return savingsAllocation;
    }

    public void setSavingsAllocation(float savingsAllocation) {
        this.savingsAllocation = savingsAllocation;
    }

    public float getRothAllocation() {
        return rothAllocation;
    }

    public void setRothAllocation(float rothAllocation) {
        this.rothAllocation = rothAllocation;
    }

    public String toString()
    {
        String eol = System.getProperty("line.separator");
        String assetString = String.format("%7.0f ,%7.0f ,%7.0f,%4.3f   ,%4.3f   ,%4.3f  ,%6.0f   ,%6.0f, %7.0f, %7.0f",
                savings, ira, roth, savingsAllocation, iraAllocation,  rothAllocation, mrd, xtraIRA_Withdrawal,capitalGain, savingsStockBasis);

        return(assetString);
    }

    public float getSavings() {
        return savings;
    }

    public void setSavings(float savings) {
        this.savings = savings;
    }

    public float getIra() {
        return ira;
    }

    public void setIra(float ira) {
        this.ira = ira;
    }

    public float getRoth() {
        return roth;
    }

    public void setRoth(float roth) {
        this.roth = roth;
    }
}
