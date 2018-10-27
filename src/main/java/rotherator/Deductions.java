package rotherator;

public class Deductions {
    float propertyTax;
    float mortgageInterest;  // Not currently used
    float otherDeductions;

    public Deductions(float propertyTax, float mortgageInterest, float otherDeductions) {
        this.propertyTax = propertyTax;
        this.mortgageInterest = mortgageInterest;
        this.otherDeductions = otherDeductions;
    }

    public float getPropertyTax() {
        return propertyTax;
    }

    public float getMortgageInterest() {
        return mortgageInterest;
    }

    public float getOtherDeductions() {
        return otherDeductions;
    }
}
