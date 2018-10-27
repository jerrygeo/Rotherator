package rotherator;

public class InflationFactor {
    float sinceFirstYear;

    public InflationFactor() {
        sinceFirstYear = 1.0F;
    }

    public float getSinceFirstYear() {
        return sinceFirstYear;
    }

    public void updateForNextYear(float thisYearInflation) {
        this.sinceFirstYear *= ( 1.0F + thisYearInflation);
    }

    public void reset(){
        this.sinceFirstYear = 1.0F;
    }
}
