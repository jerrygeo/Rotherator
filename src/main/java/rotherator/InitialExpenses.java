package rotherator;

public class InitialExpenses {
    float AGI2YearsAgo;
    float AGILastYear;
    float totalTaxLastYear;


    InitialExpenses()
    {
        AGI2YearsAgo = 0.0F;
        AGILastYear = 0.0F;
        totalTaxLastYear = 0.0F;

    }

    public float getAGI2YearsAgo() {
        return AGI2YearsAgo;
    }

    public void setAGI2YearsAgo(float AGI2YearsAgo) {
        this.AGI2YearsAgo = AGI2YearsAgo;
    }

    public float getAGILastYear() {
        return AGILastYear;
    }

    public void setAGILastYear(float AGILastYear) {
        this.AGILastYear = AGILastYear;
    }

    public float getTotalTaxLastYear() {
        return totalTaxLastYear;
    }

    public void setTotalTaxLastYear(float totalTaxLastYear) {
        this.totalTaxLastYear = totalTaxLastYear;
    }
}
