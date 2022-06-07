package csv;

import java.io.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {
        Table table = null;
        BufferedReader reader = new BufferedReader(new FileReader(csv));
        String readStr;
        String[] item;
        List<String> fixedItem;
        try {
            if (isFirstLineHeader == true) {
                String[] header = reader.readLine().split(",");
                table = new TableImpl(header);

            } else {
                String[] str = reader.readLine().split(",");
                table = new TableImpl(str.length);
            }
            readStr = reader.readLine();
            int index = 0;
            while(readStr != null) {
                // 예외처리
                readStr = readStr.replace("\"\"", "\"");
                item = readStr.split(",");
                item[3] = item[3].replace("\"", "")+ "," + item[4].substring(0, item[4].length()-1);
                fixedItem = new ArrayList<String>();
                for(int i = 0; i<item.length-1; i++) {
                    if(i<4){
                        fixedItem.add(item[i]);
                    }
                    else {
                        if(item[i+1] != null){
                            fixedItem.add(item[i+1]);
                        }
                    }
                }
                if (readStr.charAt(readStr.length()-1) == ',' )
                    fixedItem.add("");

                for (int i = 0; i < fixedItem.size(); i++) {
                    Column column = table.getColumn(i);

                    if (fixedItem.size() == i){
                        column.setValue(index, "");
                    }
                    else {
                        if (fixedItem.get(i).isEmpty()) { column.setValue(index, ""); }
                        else { column.setValue(index, fixedItem.get(i)); }
                    }
                }

                readStr = reader.readLine();
                index++;
            }
        }

        catch (IOException e){
            e.printStackTrace();
        }
        return table;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        Table sortTable = null;
        String[] headers = new String[table.getColumnCount()];

        for (int i = 0; i < table.getColumnCount(); i++) {
            headers[i] = table.getColumn(i).getHeader();
        }
        sortTable = new TableImpl(headers);
        List<ArrayList<String>> result = new ArrayList<>();


        for (int i = 0; i < table.getRowCount(); i++) {
            ArrayList<String> row = new ArrayList<>();

            for (int j = 0; j < headers.length; j++) {
                row.add(table.getColumn(j).getValue(i));
            }

            result.add(row);
        }
        sortTable = new TableImpl(headers);
        ColumnImpl column = (ColumnImpl) table.getColumn(byIndexOfColumn);
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
                        String v = String.valueOf(a.get(byIndexOfColumn) == "" ? String.valueOf("Z") : a.get(byIndexOfColumn));
                        String s = String.valueOf(b.get(byIndexOfColumn) == "" ? String.valueOf("Z") : b.get(byIndexOfColumn));
                        return v.compareTo(s);
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
        System.out.println(table.getRowCount());
        for (int j = 0; j < table.getColumnCount(); j++) {
            for (int i = 0; i <table.getRowCount(); i++) {
                Column sortColumn = sortTable.getColumn(j);
//                System.out.println(result.get(i).get(j));
                sortColumn.setValue(i, result.get(i).get(j));
            }
        }
        return sortTable;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        Table shuffleTable = null;
        String[] headers = new String[table.getColumnCount()];
        List<Integer> index = new ArrayList<>();

        for(int i=0; i< table.getRowCount(); i++) {
            index.add(i);
        }
        Collections.shuffle(index);

        for (int i = 0; i < table.getColumnCount(); i++) {
            headers[i] = table.getColumn(i).getHeader();
        }
        shuffleTable = new TableImpl(headers);

        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < headers.length; j++) {
                Column shuffleTableColumn = shuffleTable.getColumn(j);
                Column existColumn = table.getColumn(j);
                shuffleTableColumn.setValue(i, existColumn.getValue(index.get(i)));
            }
        }
        return shuffleTable;
    }
}
