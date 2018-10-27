package rotherator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A class which represents all of the tax brackets for a single tax year and figures up the tax owed
 */
public class TaxTable {



    private String tableDescription;
    private float stdDeduction;
    private float personalExemption;
    private float stateTaxDeductionLimit;  // This parameter is only useful for Federal tax. It is ignored for state tax.
    ArrayList<TaxBracket> brackets = new ArrayList<TaxBracket>();

    public TaxTable()
    {
        tableDescription = "Empty Table";
        stdDeduction = 0.0F;
        personalExemption = 0.0F;
        stateTaxDeductionLimit = 0.f;
    }

    public TaxTable(String description, float stdDeduct, float personalExemption, float stateTaxLimit )
    {
        tableDescription = description;
        stdDeduction = stdDeduct;
        this.personalExemption = personalExemption;
        stateTaxDeductionLimit = stateTaxLimit;
    }

    public String getTableDescription() {
        return tableDescription;
    }

    public float computeAnnualTax(float agi, float stateTax, Deductions deductions, float longTermCapitalGain, float bracketInflationFactor, float credits)
    {
        //     Compare state tax+property tax (up to maximum allowed) + deductions to standard deduction
        //     Pick the largest, then combine with personal exemption and subtract from agi
        //     Then look up tax in table.
        float taxableIncome = 0.F;

        float bestDeduction;
        float stateTaxDeduction = stateTax + deductions.getPropertyTax();
        if (stateTaxDeduction > stateTaxDeductionLimit) stateTaxDeduction = stateTaxDeductionLimit;
        bestDeduction = stateTaxDeduction + deductions.getOtherDeductions() + deductions.getMortgageInterest();
        if (bestDeduction < stdDeduction*bracketInflationFactor) bestDeduction = stdDeduction*bracketInflationFactor;
        taxableIncome = agi -  bestDeduction - personalExemption*Main.rawIncomeExpense.getNumberMedicarePremiums()*bracketInflationFactor;
        if (taxableIncome < 0.F) taxableIncome = 0.F;
        //   Figure long term capital gain tax using bracket defined by income with full capital gain included.
        int ndxCapGain = 0;
        while ( (ndxCapGain<brackets.size()-1) && (taxableIncome >= brackets.get(ndxCapGain).getIncomeBracket()*bracketInflationFactor)) ndxCapGain++;
        ndxCapGain--;
        float taxablePortionOfLTGain = brackets.get(ndxCapGain).getCapitalGainsRate() * longTermCapitalGain;
        //  Now compute tax on income including taxable portion of long term gain
        taxableIncome = taxableIncome - longTermCapitalGain + taxablePortionOfLTGain;
        if (taxableIncome < 0.F) taxableIncome = 0.F;
        int ndxBracket=0;
        while ( (ndxBracket<brackets.size()-1) && (taxableIncome >= brackets.get(ndxBracket).getIncomeBracket()*bracketInflationFactor)) ndxBracket++;
        ndxBracket--;
        if (taxableIncome >= brackets.get(brackets.size()-1).getIncomeBracket()*bracketInflationFactor) ndxBracket = brackets.size()-1;
        float thisBaseTax = brackets.get(ndxBracket).getBracketTaxBase()*bracketInflationFactor;
        float jerryDelta = ( taxableIncome - brackets.get(ndxBracket).getIncomeBracket()*bracketInflationFactor )*brackets.get(ndxBracket).getMarginalRate();
        float totalSmithTaxBeforeCredits = thisBaseTax+jerryDelta;
        float taxAfterCredits = totalSmithTaxBeforeCredits - credits;
        if (taxAfterCredits < 0.F) taxAfterCredits = 0.0F;
        //        System.out.println("Computing tax: i="+i);
        return (taxAfterCredits);
    }

    public String toString(){
        String eol = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append(tableDescription + eol +
                "   Std Deduction: " + String.valueOf(stdDeduction) +
                ", StateTax deduction limit: " + String.valueOf(stateTaxDeductionLimit)  + eol);
        sb.append("    Income     Base Tax    Marginal Rate" + eol);
        for (TaxBracket b: brackets )
        {
            sb.append("   " + b.toString() + eol);
        }
        return (sb.toString());
    }

    public static List<TaxTable> readTaxCSV(String filename) {
        try {
            FileReader reader = new FileReader(filename);
            return StreamSupport
                    .stream(new TaxTableReader(reader).spliterator(), false)
                    .collect(Collectors.toList());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

}
