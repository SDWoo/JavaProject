package csv;

import java.util.ArrayList;
import java.util.List;

import com.sun.security.jgss.GSSUtil;
import org.w3c.dom.ls.LSOutput;

import java.util.function.Predicate;

class TableImpl implements Table{
    private List<Column> columns;
    private String[] states = {"count", "mean", "std", "min", "25%", "50%", "75%", "max"};

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
        String printStr = "";
        String header = "";
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            header = column.getHeader();
            ColumnImpl c = (ColumnImpl) column;
            printStr += String.format(" %" + c.getFormatNum() + "s |", header);
        }
        printStr += "\n";
        for (int i = 0; i < columns.get(0).count() ; i++) {
            for (Column column: columns){
                ColumnImpl c = (ColumnImpl) column;
                if(column.getValue(i).isEmpty()){
                    printStr += String.format(" %" + c.getFormatNum() + "s |", "null");
                }
                else {
                    printStr += String.format(" %" + c.getFormatNum() + "s |", column.getValue(i));
                }
            }
            printStr += "\n";
        }

        System.out.println(printStr);
    }

    @Override
    public Table getStats() {
        String[] newHeader = new String[columns.size()+1];
        List<Integer> realIndex = new ArrayList<>();
        Table statsTable = null;

        newHeader[0] = " ";
        //새로운 헤더 만들기
        realIndex.add(1111);
        int notNullCount = 1;
        for (int i = 0; i < columns.size(); i++) {
            if(columns.get(i).isNumericColumn() || columns.get(i).getHeader().equals("Ticket")) {
                newHeader[notNullCount] = columns.get(i).getHeader();
                notNullCount++;
                realIndex.add(i);
            }
        }
        String[] fixedNewHeader = new String[notNullCount];

        //null 제거 (새 배열 복사)
        for (int i = 0; i < notNullCount; i++) {
            fixedNewHeader[i] = newHeader[i];
        }

        for(String str: fixedNewHeader) {
            System.out.print(str + ",");;
        }
        statsTable = new TableImpl(fixedNewHeader);
        // 새로운 value 만들기
        for (int i = 0; i < 8; i++) { // low
            statsTable.getColumn(0).setValue(i, states[i]);
            for (int j = 1; j < notNullCount; j++){ // column
                Column tableColumn = statsTable.getColumn(j);
                System.out.println(tableColumn.getHeader());;
                Column column = getColumn(realIndex.get(j)); //
                switch (i) {
                    case 0:
                        tableColumn.setValue(0, String.valueOf(column.getNumericCount()));
                        break;

                    case 1:
                        tableColumn.setValue( 1, String.format("%.6f", column.getMean()));
                        break;

                    case 2:
                        tableColumn.setValue(i, String.format("%.6f", column.getStd()));
                        break;

                    case 3:
                        tableColumn.setValue(i, column.getNumericMin());
                        break;

                    case 4:
                        tableColumn.setValue(i, column.getQ1());
                        break;

                    case 5:
                        tableColumn.setValue(i, column.getMedian());
                        break;

                    case 6:
                        tableColumn.setValue(i, column.getQ3());
                        break;

                    case 7:
                        tableColumn.setValue(i, column.getNumericMax());
                        break;
                }
            }
        }
        return statsTable;
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
