package rotherator;

public class SnapshotSelect {
    boolean subtractTaxes;
    int snapShotYr;

    public SnapshotSelect(boolean subtractTaxes, int snapShotYr) {
        this.snapShotYr = snapShotYr;
    }

    public int getSnapShotYr() {
        return snapShotYr;
    }

    public void setSnapShotYr(int snapShotYr) {
        this.snapShotYr = snapShotYr;
    }
}
