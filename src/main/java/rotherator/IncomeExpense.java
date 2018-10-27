package rotherator;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;

public class IncomeExpense {
    int year;
    int age;
    float socialSecurity;
    float taxableIncome;    // Does not include soc. security, IRA distributions, interest, or dividends
    float taxFreeIncome;
    float agi;              // Adjusted gross income
    float combinedIncome;   // Adjusted gross + 1/2 social security for Medicare IRMAA calculation 2 years from now
    float expenses;         // Living expenses + extra spending (car, travel, etc.)
    Deductions deductions;
    float credits;          //  Tax credits
    float medicare;
    float taxes;
    float lastYearTaxes;    // Need to be paid this year
    float dividendIncome;  // Taxable dividend income
    float interestIncome;  // Taxable interest income

    public IncomeExpense(String[] csvLine)
    {
        this.year =  Integer.parseInt(csvLine[0]);
        this.age =  Integer.parseInt(csvLine[1]);
        this.socialSecurity = Float.parseFloat(csvLine[2]);
        this.taxableIncome = Float.parseFloat(csvLine[3]);
        this.taxFreeIncome = Float.parseFloat(csvLine[4]);
        this.expenses = Float.parseFloat(csvLine[5]);
        this.deductions = new Deductions( Float.parseFloat(csvLine[6]), Float.parseFloat(csvLine[7]), Float.parseFloat(csvLine[8]));
        this.credits = Float.parseFloat(csvLine[9]);
        this.agi =0.0F;
        this.medicare = 0.0F;
        this.taxes = 0.0F;
        this.lastYearTaxes = 0.0F;
        this.dividendIncome = 0.0F;
        this.interestIncome = 0.0F;
    }

    public float getMortgageInterest() {
        return deductions.getMortgageInterest();
    }

    public float getOtherDeductions() {
        return deductions.getOtherDeductions();
    }

    public float getPropertyTax() {
        return deductions.getPropertyTax();
    }

    public float getAgi() {
        return agi;
    }

    public void setAgi(float agi) {
        this.agi = agi;
    }

    public float getCombinedIncome() {
        return combinedIncome;
    }

    public void setCombinedIncome(float combinedIncome) {
        this.combinedIncome = combinedIncome;
    }

    public float getLastYearTaxes() {
        return lastYearTaxes;
    }

    public void setLastYearTaxes(float lastYearTaxes) {
        this.lastYearTaxes = lastYearTaxes;
    }

    public float getMedicare() {
        return medicare;
    }

    public void setMedicare(float medicare) {
        this.medicare = medicare;
    }

    public float getTaxes() {
        return taxes;
    }

    public void setTaxes(float taxes) {
        this.taxes = taxes;
    }

    public float getDividendIncome() {
        return dividendIncome;
    }

    public void setDividendIncome(float dividendIncome) {
        this.dividendIncome = dividendIncome;
    }

    public float getInterestIncome() {
        return interestIncome;
    }

    public void setInterestIncome(float interestIncome) {
        this.interestIncome = interestIncome;
    }


    public IncomeExpense(IncomeExpense original )
    {
        this.year = original.year;
        this.age = original.age;
        this.socialSecurity = original.socialSecurity;
        this.taxableIncome = original.taxableIncome;
        this.taxFreeIncome = original.taxFreeIncome;
        this.combinedIncome = original.combinedIncome;

        this.expenses = original.expenses;
        this.deductions = new Deductions(original.getPropertyTax(), original.getMortgageInterest(), original.getOtherDeductions());
        this.medicare = original.medicare;
        this.taxes = original.taxes;
        this.lastYearTaxes = original.lastYearTaxes;
        this.dividendIncome = original.dividendIncome;
        this.interestIncome = original.interestIncome;
    }

    public String toString()
    {
        String eol = System.getProperty("line.separator");
        float investmentIncome = dividendIncome + interestIncome;
        String incomeExpenseString = String.format("%3d   ,%6.0f   ,%7.0f     ,%7.0f   ,%7.0f,     %7.0f   ,%7.0f  ,%6.0f, %6.0f, %6.0f, %7.0f  ,%7.0f, %7.0f",
                age,
                socialSecurity,
                taxableIncome,
                taxFreeIncome,
                investmentIncome,
                agi,
                taxes,
                medicare,
                expenses ,
                deductions.getPropertyTax(),
                deductions.getMortgageInterest(),
                deductions.getOtherDeductions(),
                credits);
        return(incomeExpenseString);
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public float getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(float socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public float getTaxableIncome() {
        return taxableIncome;
    }

    public void setTaxableIncome(float taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public float getTaxFreeIncome() {
        return taxFreeIncome;
    }

    public void setTaxFreeIncome(float taxFreeIncome) {
        this.taxFreeIncome = taxFreeIncome;
    }

    public float getExpenses() {
        return expenses;
    }

    public void setExpenses(float expenses) {
        this.expenses = expenses;
    }

    public float getCredits() {
        return credits;
    }

    public void setCredits(float credits) {
        this.credits = credits;
    }

    public static RawIncomeExpense ReadIncomeExpenseCSV(boolean UNIT_TEST) {
        char[] charCheck = new char[100];  // Used to check if string is digits
        RawIncomeExpense rawIncomeExpense = new RawIncomeExpense();

//        List<IncomeExpense> incomeExpense = new ArrayList<IncomeExpense>();

        try {
            CSVReader reader = new CSVReader(new FileReader("Income-Expense.csv"));
            String[] nextLine;  // nextLine[] is an array of values from the line

            nextLine = reader.readNext();  // First line is just text
            while ((nextLine = reader.readNext()) != null) {
                charCheck = nextLine[0].toCharArray();
                if (nextLine[0].toUpperCase().equals("TARGETALLOCATION="))
                    rawIncomeExpense.targetAllocation = Float.parseFloat(nextLine[1]);
                else if (nextLine[0].toUpperCase().equals("INITIALIRA="))
                    rawIncomeExpense.initialAssets.setIra(Float.parseFloat(nextLine[1]));
                else if (nextLine[0].toUpperCase().equals("INITIALSAVINGS="))
                    rawIncomeExpense.initialAssets.setSavings(Float.parseFloat(nextLine[1]));
                else if (nextLine[0].toUpperCase().equals("INITIALROTH=")) {
                    rawIncomeExpense.initialAssets.setRoth(Float.parseFloat(nextLine[1]));

//                    System.out.println("TargetAllocation="+targetAllocation+" Savings="+newAsset.savings+" IRA="+newAsset.ira+" Roth="+newAsset.roth);
                } else if (nextLine[0].toUpperCase().equals("INITIALSTOCKBASIS="))
                    rawIncomeExpense.initialAssets.setSavingsStockBasis(Float.parseFloat(nextLine[1]));
                else if (nextLine[0].toUpperCase().equals("AGI2YEARSAGO="))
                    rawIncomeExpense.initialExpenses.setAGI2YearsAgo(Float.parseFloat(nextLine[1]));
                else if (nextLine[0].toUpperCase().equals("AGILASTYEAR="))
                    rawIncomeExpense.initialExpenses.setAGILastYear(Float.parseFloat(nextLine[1]));
                else if (nextLine[0].toUpperCase().equals("TOTALTAXFORLASTYEAR="))
                    rawIncomeExpense.initialExpenses.setTotalTaxLastYear(Float.parseFloat(nextLine[1]));
                else if (nextLine[0].toUpperCase().equals("NUMBERMEDICAREPREMIUMS="))
                    rawIncomeExpense.setNumberMedicarePremiums(Integer.parseInt(nextLine[1]));
                else if (Character.isDigit(charCheck[0])) {
                    rawIncomeExpense.incomeExpense.add(new IncomeExpense(nextLine));
                }
            }
            reader.close();
            if (UNIT_TEST) {
                System.out.println("Expense:");
                for (IncomeExpense item : rawIncomeExpense.incomeExpense) {
                    System.out.println(item);
                }
            }

            if ((rawIncomeExpense.targetAllocation == 0) ||
                    ((rawIncomeExpense.initialAssets.savings == 0) &&
                            (rawIncomeExpense.initialAssets.ira == 0) &&
                            (rawIncomeExpense.initialAssets.roth == 0))) {
                System.out.println("Something is wrong with Income-Expense.csv file");
                System.out.println("  Too many of these are 0.: TargetAllocation=" + rawIncomeExpense.targetAllocation +
                        " Savings=" + rawIncomeExpense.initialAssets.savings +
                        " IRA=" + rawIncomeExpense.initialAssets.ira +
                        " Roth=" + rawIncomeExpense.initialAssets.roth);
                System.exit(-1);
//               throw new Exception("Something is wrong with Income-Expense.csv file");
            }

        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        }
        return rawIncomeExpense;
    }

}
