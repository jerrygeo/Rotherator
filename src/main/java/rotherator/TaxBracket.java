package rotherator;

public class TaxBracket {
    private float incomeBracket;
    private float bracketTaxBase;
    private float marginalRate;
    private float capitalGainsRate;

    public TaxBracket(
            float incomeBracket,
            float bracketTaxBase,
            float marginalRate,
            float capitalGains) {
        this.incomeBracket = incomeBracket;
        this.bracketTaxBase = bracketTaxBase;
        this.marginalRate = marginalRate;
        this.capitalGainsRate = capitalGains;
    }

    public TaxBracket()
    {
        this.incomeBracket = 0.0F;
        this.bracketTaxBase = 0.0F;
        this.marginalRate = 0.0F;
        this.capitalGainsRate = 0.0F;
    }

    public String toString(){
        return( String.format("%8.0f    %7.0f       %4.2f      %4.2f", incomeBracket, bracketTaxBase, marginalRate, capitalGainsRate));
    }

    public float getIncomeBracket() {
        return incomeBracket;
    }

    public void setIncomeBracket(float incomeBracket) {
        this.incomeBracket = incomeBracket;
    }

    public float getBracketTaxBase() {
        return bracketTaxBase;
    }

    public void setBracketTaxBase(float bracketTaxBase) {
        this.bracketTaxBase = bracketTaxBase;
    }

    public float getMarginalRate() {
        return marginalRate;
    }

    public void setMarginalRate(float marginalRate) {
        this.marginalRate = marginalRate;
    }

    public float getCapitalGainsRate() {
        return capitalGainsRate;
    }

    public void setCapitalGainsRate(float capitalGainsRate) {
        this.capitalGainsRate = capitalGainsRate;
    }
}
