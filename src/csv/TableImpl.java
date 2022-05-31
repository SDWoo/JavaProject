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

        return "<" + getClass().getInterfaces()[0].getName() + "@" + hashCode()+">\n" +
                "RangeIndex: " +
                columns.get(0).count();
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
