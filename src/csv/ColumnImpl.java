package csv;

class ColumnImpl implements Column{
    @Override
    public String getHeader() {
        return null;
    }

    @Override
    public String getValue(int index) {
        return null;
    }

    @Override
    public <T extends Number> T getValue(int index, Class<T> t) {
        return null;
    }

    @Override
    public void setValue(int index, String value) {

    }

    @Override
    public <T extends Number> void setValue(int index, T value) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public void print() {

    }

    @Override
    public boolean isNumericColumn() {
        return false;
    }

    @Override
    public long getNullCount() {
        return 0;
    }

    @Override
    public long getNumericCount() {
        return 0;
    }

    @Override
    public double getNumericMin() {
        return 0;
    }

    @Override
    public double getNumericMax() {
        return 0;
    }

    @Override
    public double getMean() {
        return 0;
    }

    @Override
    public double getStd() {
        return 0;
    }

    @Override
    public double getQ1() {
        return 0;
    }

    @Override
    public double getMedian() {
        return 0;
    }

    @Override
    public double getQ3() {
        return 0;
    }

    @Override
    public boolean fillNullWithMean() {
        return false;
    }

    @Override
    public boolean fillNullWithZero() {
        return false;
    }

    @Override
    public boolean standardize() {
        return false;
    }

    @Override
    public boolean normalize() {
        return false;
    }

    @Override
    public boolean factorize() {
        return false;
    }
}
