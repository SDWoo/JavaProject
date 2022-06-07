package csv;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class TableImpl implements Table{
    private List<Column> columns;
    private String[] states = {"count", "mean", "std", "min", "25%", "50%", "75%", "max"};
    private List<String> headers = new ArrayList<>();
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
            headers.add(header[i]);
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


        for (int i = 0; i < getRowCount() ; i++) {
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
                    double round = Math.round(column.getMean() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(1, str);

                }
                // std
                else if(i == 2) {
                    double round = Math.round(column.getStd() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(2, str);
                }
                // min
                else if(i == 3) {
                    double round = Math.round(column.getNumericMin() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(3, str);
                }
                // 25%
                else if(i == 4) {
                    double round = Math.round(column.getQ1() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(4, str);
                }
                // 50%
                else if(i == 5) {
                    double round = Math.round(column.getMedian() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(5, str);

                }
                // 75%
                else if(i == 6) {
                    double round = Math.round(column.getQ3() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(6, str);
                }
                // max
                else if(i == 7) {
                    double round = Math.round(column.getNumericMax() * 1_000_000) / 1_000_000.0;
                    String str = String.valueOf(round);

                    inputColumn.setValue(7, str);
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

        Table selectedTable = null;
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }
        selectedTable = new TableImpl(headers);

        ColumnImpl col = (ColumnImpl) columns.stream()
                .filter(column -> column.getHeader().equals(columnName))
                .findAny().get();

        ArrayList<Integer> seqList = new ArrayList<>();

        for (int i = 0; i < col.getItems().size(); i++) {
            if (col.getClazz().isAssignableFrom(String.class)) {
                if (predicate.test((T) col.getValue(i))) {
                    seqList.add(i);
                }
            } else {
                if (!col.getValue(i).equals("")) {
                    Double floor;
                    try {
                        floor = Math.floor(Integer.parseInt(col.getValue(i)));
                        if (predicate.test((T) (Integer)floor.intValue())) {
                            seqList.add(i);
                        }
                    } catch (Exception e) {
                        floor = Math.floor(Double.parseDouble(col.getValue(i)));
                        if (predicate.test((T) floor)) {
                            seqList.add(i);
                        }
                    }
                }
            }

        }

        for (int i = 0; i < seqList.size(); i++) {
            for (int j = 0; j < headers.length; j++) {
                selectedTable.getColumn(j).setValue(i, this.getColumn(j).getValue(seqList.get(i)));
            }
        }
        return selectedTable;
    }

    @Override
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        String[] headers = new String[columns.size()];

        for (int i = 0; i < columns.size(); i++) {
            headers[i] = columns.get(i).getHeader();
        }
        List<ArrayList<String>> result = new ArrayList<>();

        Column length = getColumn(1);
        for (int i = 0; i < length.count(); i++) {
            ArrayList<String> row = new ArrayList<>();

            for (int j = 0; j < headers.length; j++) {
                row.add(getColumn(j).getValue(i));
            }

            result.add(row);
        }

        ColumnImpl column = (ColumnImpl) columns.get(byIndexOfColumn);
        Class clazz = column.getClazz();

        if (isAscending) {
            result.sort((a, b) -> {
                if (isNullFirst) {
                    if (clazz.equals(String.class)) {
                        return a.get(byIndexOfColumn).compareTo(b.get(byIndexOfColumn));
                    } else if (clazz.equals(Double.class)) {
                        double v = Double.parseDouble(a.get(byIndexOfColumn) == "" ? "0" : a.get(byIndexOfColumn));
                        double s = Double.parseDouble(b.get(byIndexOfColumn) == "" ? "0" : b.get(byIndexOfColumn));
                        return Double.compare(v, s);
                    } else if (clazz.equals(Integer.class)) {
                        int v = Integer.parseInt(a.get(byIndexOfColumn) == "" ? "0" : a.get(byIndexOfColumn));
                        int s = Integer.parseInt(b.get(byIndexOfColumn) == "" ? "0" : b.get(byIndexOfColumn));
                        return Integer.compare(v, s);
                    }
                } else {
                    if (clazz.equals(String.class)) {
                        return a.get(byIndexOfColumn).compareTo(b.get(byIndexOfColumn));
                    } else if (clazz.equals(Double.class)) {
                        double v = Double.parseDouble(a.get(byIndexOfColumn) == "" ? String.valueOf(Double.MAX_VALUE) : a.get(byIndexOfColumn));
                        double s = Double.parseDouble(b.get(byIndexOfColumn) == "" ? String.valueOf(Double.MAX_VALUE) : b.get(byIndexOfColumn));
                        return Double.compare(v, s);
                    } else if (clazz.equals(Integer.class)) {
                        int v = Integer.parseInt(a.get(byIndexOfColumn) == "" ? "0" : a.get(byIndexOfColumn));
                        int s = Integer.parseInt(b.get(byIndexOfColumn) == "" ? "0" : b.get(byIndexOfColumn));
                        return Integer.compare(v, s);
                    }
                }
                return 0;
            });
        }else{
            result.sort((b, a) -> {
                if (!isNullFirst) {
                    if (clazz.equals(String.class)) {
                        return a.get(byIndexOfColumn).compareTo(b.get(byIndexOfColumn));
                    } else if (clazz.equals(Double.class)) {
                        double v = Double.parseDouble(a.get(byIndexOfColumn) == "" ? "0" : a.get(byIndexOfColumn));
                        double s = Double.parseDouble(b.get(byIndexOfColumn) == "" ? "0" : b.get(byIndexOfColumn));
                        return Double.compare(v, s);
                    } else if (clazz.equals(Integer.class)) {
                        int v = Integer.parseInt(a.get(byIndexOfColumn) == "" ? "0" : a.get(byIndexOfColumn));
                        int s = Integer.parseInt(b.get(byIndexOfColumn) == "" ? "0" : b.get(byIndexOfColumn));
                        return Integer.compare(v, s);
                    }
                } else {
                    if (clazz.equals(String.class)) {
                        return a.get(byIndexOfColumn).compareTo(b.get(byIndexOfColumn));
                    } else if (clazz.equals(Double.class)) {
                        double v = Double.parseDouble(a.get(byIndexOfColumn) == "" ? String.valueOf(Double.MAX_VALUE) : a.get(byIndexOfColumn));
                        double s = Double.parseDouble(b.get(byIndexOfColumn) == "" ? String.valueOf(Double.MAX_VALUE) : b.get(byIndexOfColumn));
                        return Double.compare(v, s);
                    } else if (clazz.equals(Integer.class)) {
                        int v = Integer.parseInt(a.get(byIndexOfColumn) == "" ? String.valueOf(Integer.MAX_VALUE) : a.get(byIndexOfColumn));
                        int s = Integer.parseInt(b.get(byIndexOfColumn) == "" ? String.valueOf(Integer.MAX_VALUE) : b.get(byIndexOfColumn));
                        return Integer.compare(v, s);
                    }
                }
                return 0;
            });
        }

        for (int j = 0; j < headers.length; j++) {
                ColumnImpl column1 = (ColumnImpl) this.getColumn(j);
                column1.getItems().clear();
            for (int i = 0; i <891; i++) {
                Column sortColumn = this.getColumn(j);
                sortColumn.setValue(i, result.get(j).get(i-1));
            }
        }
        return this;
    }

    @Override
    public Table shuffle() {
        List<Integer> index = new ArrayList<>();
        for(int i=1; i<getRowCount(); i++) {
            index.add(i);
        }
        Collections.shuffle(index);


        for (int i = 0; i < getColumnCount(); i++) {
            for (int j = 0; j < getRowCount(); j++) {
                Column shuffleColumn = this.getColumn(i);
                shuffleColumn.setValue(index.get(j), getColumn(i).getValue(index.get(j)));
            }
        }
        return this;
    }

    @Override
    public int getRowCount() {
        return getColumn(1).count();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public Column getColumn(int index) {
        Column column = columns.get(index);
        return column;
    }

    @Override
    public Column getColumn(String name) {
        System.out.println(name);

        Column column = columns.get(this.headers.indexOf(name));
        return column;
    }

    @Override
    public boolean fillNullWithMean() {

        AtomicBoolean check = new AtomicBoolean(false);

        List<Column> columns = this.columns.stream()
                .filter(column -> {
                    ColumnImpl impl = (ColumnImpl) column;
                    return impl.fillNullWithMean();
                })
                .collect(Collectors.toList());


        for (int i=0;i<columns.size();i++) {
            ColumnImpl col = (ColumnImpl) columns.get(i);
            double mean = col.getMean();
            int count = 0;
            for (int j = 0; j < col.getItems().size(); j++) {
                if (col.getItems().get(j).equals("")) {
                    check.set(true);
                    count++;
                    col.setValue(j, mean);
                    if (col.getClazz().isAssignableFrom(Integer.class)) {
                        col.setType("double", Double.class);
                    }
                }
            }
            col.setNotNullNumber(col.getItems().size() - col.getNullCount() + count);

        }

        return check.get();
    }

    @Override
    public boolean fillNullWithZero() {
        AtomicBoolean check = new AtomicBoolean(false);

        List<Column> columns = this.columns.stream()
                .filter(column -> {
                    ColumnImpl impl = (ColumnImpl) column;
                    return impl.fillNullWithZero();
                })
                .collect(Collectors.toList());


        for (int i=0;i<columns.size();i++) {
            ColumnImpl col = (ColumnImpl) columns.get(i);
            int count = 0;
            for (int j = 0; j < col.getItems().size(); j++) {
                if (col.getItems().get(j).equals("")) {
                    check.set(true);
                    count++;
                    col.setValue(j, 0);
                }
            }
            col.setNotNullNumber(col.getItems().size() - col.getNullCount() + count);

        }

        return check.get();
    }

    @Override
    public boolean standardize() {
        AtomicBoolean check = new AtomicBoolean(false);

        List<Column> columns = this.columns.stream()
                .filter(column -> {
                    ColumnImpl impl = (ColumnImpl) column;
                    return impl.standardize();
                })
                .collect(Collectors.toList());


        for (int i=0;i<columns.size();i++) {
            ColumnImpl col = (ColumnImpl) columns.get(i);
            double mean = col.getMean();
            double std = col.getStd();
            for (int j = 0; j < col.getItems().size(); j++) {
                if (!col.getItems().get(j).equals("")) {
                    check.set(true);

                    col.setValue(j, ((Double.valueOf(col.getValue(j)) - mean) / std));
                    if (col.getClazz().isAssignableFrom(Integer.class)) {
                        col.setType("double", Double.class);
                    }
                }
            }

        }

        return check.get();
    }

    @Override
    public boolean normalize() {
        AtomicBoolean check = new AtomicBoolean(false);

        List<Column> columns = this.columns.stream()
                .filter(column -> {
                    ColumnImpl impl = (ColumnImpl) column;
                    return impl.standardize();
                })
                .collect(Collectors.toList());


        for (int i=0;i<columns.size();i++) {
            ColumnImpl col = (ColumnImpl) columns.get(i);
            double min = col.getNumericMin();
            double max = col.getNumericMax();
            for (int j = 0; j < col.getItems().size(); j++) {
                if (!col.getItems().get(j).equals("")) {
                    check.set(true);

                    col.setValue(j, ((Double.valueOf(col.getValue(j)) - min) / (max-min)));
                    if (col.getClazz().isAssignableFrom(Integer.class)) {
                        col.setType("double", Double.class);
                    }
                }
            }

        }

        return check.get();
    }

    @Override
    public boolean factorize() {
        return false;
    }
}
