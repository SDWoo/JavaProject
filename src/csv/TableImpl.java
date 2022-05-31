package csv;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.ls.LSOutput;

import java.util.function.Predicate;

class TableImpl implements Table{
    private String toStr;
    private List<Column> columns;

    @Override
    public String toString() {
        String s2, s3, s4;
        int d=0, in=0, s=0;
        String answer = "";
        String  a = "";
        answer += String.format(" %"+ 2 + "s |", "#");
        answer += String.format(" %"+ 11 + "s |", "Column");
        answer += String.format(" %"+ 14 + "s |", "Non-Null Count");
        answer += "DType \n";
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            ColumnImpl c = (ColumnImpl) column;
            s2 = column.getHeader();
            if(columns.get(i).count() == 0) {
                s3 = String.valueOf(columns.get(i).count()) + " null";
            }
            else {
                s3 = String.valueOf(columns.get(i).count()) + " non-null";
            }
            s4 = c.getType();
            switch (s4){
                case "double":
                    d++;
                    break;
                case "int":
                    in++;
                    break;
                case "String":
                    s++;
                    break;
                default:
                    break;
            }
            answer += String.format(" %" + 2 + "d |", i);
            answer += String.format(" %" + 11 + "s |", s2);
            answer += String.format(" %" + 14 + "s |", s3);
            answer += s4 + "\n";
        }
        answer += "dtypes: double(" + d + "), int(" + in + "), String(" + s +")";

        return "<" + getClass().getInterfaces()[0].getName() + "@" + Integer.toHexString(hashCode())+">\n" +
                "RangeIndex: "+ columns.get(0).count() + " entries, 0 to " + (columns.get(0).count()-1) + "\n"+
                "Data columns (total " + columns.size() + " columns): \n"
                + answer;

    }
    // header가 있을 경우 생성자
    public TableImpl(String[] header){
        columns = new ArrayList<>();
        for (int i = 0; i < header.length; i++) {
            Column column = new ColumnImpl(header[i]);
            columns.add(column);
        }
    }
    // header가 없을 경우 생성자
    public TableImpl(int length){
        columns = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Column column = new ColumnImpl();
            columns.add(column);
        }

    }
    @Override
    public void print() {

    }

    @Override
    public Table getStats() {
        return null;
    }

    @Override
    public Table head() {
        return null;
    }

    @Override
    public Table head(int lineCount) {
        return null;
    }

    @Override
    public Table tail() {
        return null;
    }

    @Override
    public Table tail(int lineCount) {
        return null;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public Table selectRowsAt(int... indices) {
        return null;
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        return null;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        return null;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        return null;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        return null;
    }

    @Override
    public Table shuffle() {
        return null;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public Column getColumn(int index) {
        Column column = columns.get(index);
        return column;
    }

    @Override
    public Column getColumn(String name) {
        return null;
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
