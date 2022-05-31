package csv;

import java.io.*;
import java.io.FileNotFoundException;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {
        Table table = null;
        BufferedReader reader = new BufferedReader(new FileReader(csv));
        int length;
        String readStr;
        String[] item;
        String[] fixedItem;
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
                readStr = readStr.replace("\"\"", "\"");
                System.out.println(readStr);
                item = readStr.split(",");
                item[3] = item[3].replace("\"", "")+ "," + item[4].substring(0, item[4].length()-1);
                fixedItem = new String[item.length-1];
                for(int i = 0; i<item.length-1; i++) {
                    if(i<4){
                        fixedItem[i] = item[i];
                    }
                    else {
                        if(item[i+1] != null){
                            fixedItem[i] = item[i+1];
                        }
                    }
                }
                int tokenLength = fixedItem.length;

                if (readStr.charAt(readStr.length()-1) == ',' )
                    tokenLength++;

                for (int i = 0; i < tokenLength; i++) {
                    Column column = table.getColumn(i);

                    if (fixedItem.length == i){
                        column.setValue(index, "");
                    }
                    else {
                        if (fixedItem[i].isEmpty()) { column.setValue(index, ""); }
                        else { column.setValue(index, fixedItem[i]); }
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
        return null;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        return null;
    }
}
