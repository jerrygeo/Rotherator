package rotherator;

public class Economy1Yr {
    int year;
    float inflationRate;
    float interestRate;
    float stockGrowthRate;
    float dividendRate;
    int taxTableIndex;     // Specifies which of the 4 tax tables to use for this year
    float cumulativeInflation;


    public Economy1Yr(String[] csvLine)
    {
        this.year =  Integer.parseInt(csvLine[0]);
        this.inflationRate = Float.parseFloat(csvLine[1]);
        this.interestRate = Float.parseFloat(csvLine[2]);
        this.stockGrowthRate = Float.parseFloat(csvLine[3]);
        this.dividendRate = Float.parseFloat(csvLine[4]);
        this.taxTableIndex = Integer.parseInt(csvLine[5]) - 1;  // Table index starts at 0, but user starts at 1 in CSV file
        this.cumulativeInflation = 0.0F;
    }

    public float getCumulativeInflation() {
        return cumulativeInflation;
    }

    public void setCumulativeInterest(float cumulativeInterest) {
        this.cumulativeInflation = cumulativeInterest;
    }

    public float getDividendRate() {
        return dividendRate;
    }

    public void setDividendRate(float dividendRate) {
        this.dividendRate = dividendRate;
    }

    public String toString()
    {
        String eol = System.getProperty("line.separator");
        // Need to add 1 to taxTableIndex since user starts counting at 1, but we subtracted 1 when we read it in
        return(String.valueOf(this.inflationRate) + "       ," + String.valueOf(this.interestRate)
                + "       ,"+ String.valueOf(this.stockGrowthRate) + "    ," + String.valueOf(this.dividendRate) + " ," + String.valueOf(this.taxTableIndex+1) );
    }
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(float inflationRate) {
        this.inflationRate = inflationRate;
    }

    public float getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(float interestRate) {
        this.interestRate = interestRate;
    }

    public float getStockGrowthRate() {
        return stockGrowthRate;
    }

    public void setStockGrowthRate(float stockGrowthRate) {
        stockGrowthRate = stockGrowthRate;
    }

    public int getTaxTableIndex() {
        return taxTableIndex;
    }

    public void setTaxTableIndex(int taxTableIndex) {
        this.taxTableIndex = taxTableIndex;
    }



}
