package rotherator;

public class RothXfer1Yr {
    int year;
    String XferDescription;
    float rothXferThisYear;

    public RothXfer1Yr(String[] csvLine)
    {
        this.year =  Integer.parseInt(csvLine[0]);
        this.rothXferThisYear = Float.parseFloat(csvLine[1]);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public float getRothXferThisYear() {
        return rothXferThisYear;
    }

    public void setRothXferThisYear(float rothXferThisYear) {
        this.rothXferThisYear = rothXferThisYear;
    }
}


