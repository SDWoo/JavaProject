package csv;

import java.lang.reflect.Array;
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

        Column values  = getColumn(1);
        for (int i = 0; i < values.count() ; i++) {
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

        // 다른 header들의 정보도 들어있기 때문에 실제 header의 index가 필요함
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

        statsTable = new TableImpl(fixedNewHeader);
        // 새로운 value 만들기
        for (int i = 0; i < 8; i++) { // low
                statsTable.getColumn(0).setValue(i, states[i]);
            for (int j = 1; j < notNullCount; j++){ // column
//                statsTable.getColumn(0).setValue(i, states[i]);
                Column inputColumn = statsTable.getColumn(j); // 현재 header로 값을 넣기 위함
                Column column = getColumn(realIndex.get(j)); // header의 실제 index에서 값을 받아오기 위함
                // count
                if(i == 0) {
                    inputColumn.setValue(0, String.valueOf(column.getNumericCount()));
                }
                // mean
                else if(i == 1) {
                        inputColumn.setValue(1, String.format("%f", column.getMean()));
                }
                // std
                else if(i == 2) {
                        inputColumn.setValue(2, String.format("%f", column.getStd()));
                }
                // min
                else if(i == 3) {
                        inputColumn.setValue(3, String.format("%.1f", column.getNumericMin()));
                }
                // 25%
                else if(i == 4) {
                        inputColumn.setValue(3, String.format("%.1f", column.getQ1()));
                }
                // 50%
                else if(i == 5) {
                        inputColumn.setValue(3, String.format("%f", column.getMedian()));

                }
                // 75%
                else if(i == 6) {
                    inputColumn.setValue(3, String.format("%.1f", column.getQ3()));
                }
                // max
                else if(i == 7) {
                        inputColumn.setValue(3, String.format("%.1f", column.getNumericMax()));
                }

            }
        }
        return statsTable;
    }

    // 5개 가져오기
    @Override
    public Table head() {
        Table headTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        headTable = new TableImpl(headers);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = headTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(i, column.getValue(i));
            }
        }
        return headTable;
    }

    @Override
    public Table head(int lineCount) {
        Table headIntTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        headIntTable = new TableImpl(headers);

        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = headIntTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(i, column.getValue(i));
            }
        }
        return headIntTable;
    }

    @Override
    public Table tail() {
        Table tailTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        tailTable = new TableImpl(headers);
        int index= 0;
        for (int i = 886; i < 891; i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = tailTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(index, column.getValue(i));
            }
            index++;
        }
        return tailTable;
    }

    @Override
    public Table tail(int lineCount) {
        Table tailIntTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        tailIntTable = new TableImpl(headers);
        int index= 0;
        for (int i = 891-lineCount; i < 891; i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = tailIntTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(index, column.getValue(i));
            }
            index++;
        }
        return tailIntTable;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        Table selectTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        selectTable = new TableImpl(headers);
        int index= 0;
        for (int i = beginIndex; i < endIndex; i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = selectTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(index, column.getValue(i));
            }
            index++;
        }
        return selectTable;
    }

    @Override
    public Table selectRowsAt(int... indices) {
        Table selectAtTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }

        selectAtTable = new TableImpl(headers);
        int index= 0;
        for (int i = 0; i < indices.length; i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = selectAtTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(index, column.getValue(indices[i]));
            }
            index++;
        }
        return selectAtTable;
    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        Table selectColumnTable = null;
        String[] headers = new String[endIndex-beginIndex];

        for (int i = beginIndex; i < endIndex; i++) {
            headers[i] = columns.get(i).getHeader();
        }
        selectColumnTable = new TableImpl(headers);

        Column length = getColumn(1);
        for (int i = 0; i < length.count(); i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = selectColumnTable.getColumn(j);
                Column column = getColumn(j);
                InputColumn.setValue(i, column.getValue(i));
            }
        }
        return selectColumnTable;
    }

    @Override
    public Table selectColumnsAt(int... indices) {
        Table selectColumnAtTable = null;
        String[] headers = new String[indices.length];

        for (int i = 0; i < indices.length; i++) {
            headers[i] = columns.get(indices[i]).getHeader();
        }
        selectColumnAtTable = new TableImpl(headers);

        Column length = getColumn(1);
        for (int i = 0; i < length.count(); i++) {
            for (int j = 0; j < headers.length; j++) {
                Column InputColumn = selectColumnAtTable.getColumn(j);
                Column column = getColumn(indices[j]);
                InputColumn.setValue(i, column.getValue(i));
            }
        }
        return selectColumnAtTable;
    }

    @Override
    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        return null;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        Table sortTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }
        sortTable = new TableImpl(headers);

        List<ArrayList<String>> result = new ArrayList<>();

        Column length = getColumn(1);
        for (int i = 0; i < length.count(); i++) {
            ArrayList<String> row = new ArrayList<>();
            for (int j = 0; j < headers.length; j++) {
                Column column1 = getColumn(j);
                row.add(column1.getValue(i));
            }
            result.add(row);
        }
        Column c = sortTable.getColumn(byIndexOfColumn);

        Column column = columns.get(byIndexOfColumn);
        ColumnImpl column1 = (ColumnImpl) column;
        String type = column1.getType();


        result.sort((a,b) -> {
            // 오름차순
            if (isAscending) {
                if (isNullFirst) {
                    return a.get(byIndexOfColumn).compareTo(b.get(byIndexOfColumn));
                }else {
                    if(a.get(byIndexOfColumn).equals("")) {
                        return -1;
                    }
                    else {
                        return a.get(byIndexOfColumn).compareTo(b.get(byIndexOfColumn));
                    }
                }

            }
            // 내림차순
            else {
                return b.get(byIndexOfColumn).compareTo(a.get(byIndexOfColumn));
            }
        });
        for(ArrayList<String> list:result) {
            System.out.println(list);
        }
        return sortTable;
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
