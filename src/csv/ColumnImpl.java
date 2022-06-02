package csv;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ColumnImpl implements Column{
    private int index;
    private int notNullNumber;
    private int numberCount;
    private String type;
    private String header;
    private List<String> Items;
    private int formatNum;
    private List<Double> doubles; // mean, std, max, min 을 위함

    ColumnImpl(String header){
        this.header = header;
        this.type = "";
        this.index = 0;
        this.notNullNumber = 0;
        this.numberCount = 0;
        Items = new ArrayList<String>();
        switch (header) {
            case "Name":
                this.formatNum = 70;
                break;
            case "Age":
                this.formatNum = 4;
                break;
            case " ":
                this.formatNum = 2;
                break;
            default:
                this.formatNum = header.length();
                break;
        }
    }
    ColumnImpl(){
        this.header = null;
        this.type = "";
        this.index = 0;
        this.notNullNumber = 0;
        this.numberCount = 0;
        Items = new ArrayList<String>();
    }

    private boolean isDouble(String num)
    {
        try
        {
            Double.parseDouble(num);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
    private boolean isInteger(String num)
    {
        try
        {
            Integer.parseInt(num);
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }
    private static boolean isEmptyOrNull(String str) {
        if (str != null && !str.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    int getFormatNum() { return formatNum;}
    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public String getValue(int index) {
        return Items.get(index);
    }

    @Override
    public <T extends Number> T getValue(int index, Class<T> t) {
        return null;
    }

    @Override
    public void setValue(int index, String value) {
        if (isEmptyOrNull(value)){

        }
        else if(isInteger(value)){
            notNullNumber += 1;
            numberCount += 1;
            formatNum = Math.max(formatNum, value.length());
            if (!type.equals("String") && !type.equals("double")) {
                type = "int";
            }
        }
        else if(isDouble(value)){
            notNullNumber += 1;
            numberCount += 1;
            formatNum = Math.max(formatNum, value.length());
            if (!type.equals("String")) {
                type = "double";
            }

        }else {
            notNullNumber += 1;
            formatNum = Math.max(formatNum, value.length());
            type = "String";
        }
        Items.add(index, value);
    }

    @Override
    public <T extends Number> void setValue(int index, T value) {

    }

    @Override
    public int count() {
        return numberCount;
    }

    @Override
    public void print() {

    }

    @Override
    public boolean isNumericColumn() {
        if (!type.equals("String")) {
            return true;
        }
        else {return false;}
    }

    @Override
    public long getNullCount() {
        return 0;
    }
    String getType() {
        return type;
    }
    @Override
    public long getNumericCount() {
        return numberCount;
    }

    @Override
    public double getNumericMin() {
        double min = 0;
        int a = 0;
        if (Items.size() != 0) {
            doubles = new ArrayList<>();

            for (int i = 0; i < Items.size(); i++) {
                try {
                    doubles.add(Double.valueOf(Items.get(i)));
                }
                catch (NumberFormatException e){
                    a++;
                }
            }
            min = Collections.max(doubles);
        }
        return min;

    }

    @Override
    public double getNumericMax() {
        doubles = new ArrayList<>();
        int a = 0;
        for (int i =0; i< Items.size(); i++){
            try{
                doubles.add(Double.valueOf(Items.get(i)));
            }
            catch (NumberFormatException e) {
                a++;
            }
        }
        double max = Collections.min(doubles);
        return max;
    }

    @Override
    public double getMean() {

        double sum = 0;
        for (int i = 0; i < Items.size(); i++) {
            try {
                sum += Double.valueOf(Items.get(i));
            }
            catch (NumberFormatException e) {
                sum += 0.0;
            }
        }
        double mean = sum/numberCount;
        return mean;
    }

    @Override
    public double getStd() {
        doubles = new ArrayList<>();
        double sum = 0;
        double std = 0;
        for (int i = 0; i < Items.size(); i++) {
            try {

            doubles.add(Double.valueOf(Items.get(i)));
            sum += Double.valueOf(Items.get(i));
            }
            catch (NumberFormatException e) {
                sum += 0.0;
            }
        }
        double mean = sum/numberCount;

        double devSum = 0;
        for(Double num: doubles){
            devSum += Math.pow((num - mean),2);
        }
        std = Math.sqrt(devSum/numberCount);
        return std;
    }

    @Override
    public double getQ1() {
        doubles = new ArrayList<>();
        int a = 0;
        for (int i = 0; i < Items.size(); i++) {
            try {
                doubles.add(Double.valueOf(Items.get(i)));
            }
            catch (NumberFormatException e) {
                a++;
            }
        }
        Collections.sort(doubles);

        return doubles.get((doubles.size()/4) - 1);
    }

    @Override
    public double getMedian() {
        doubles = new ArrayList<>();
        int a = 0;
        for (int i = 0; i < Items.size(); i++) {
            try {
                doubles.add(Double.valueOf(Items.get(i)));
            }
            catch (NumberFormatException e) {
                a++;
            }
        }
        Collections.sort(doubles);
        return doubles.get((doubles.size()/2) - 1);
    }

    @Override
    public double getQ3() {
        doubles = new ArrayList<>();
        int a = 0;
        for (int i = 0; i < Items.size(); i++) {
            try {
                doubles.add(Double.valueOf(Items.get(i)));
            }
            catch (NumberFormatException e) {
                a++;
            }
        }
        Collections.sort(doubles);
        return doubles.get((doubles.size()/2) + (doubles.size()/4) - 1);
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
