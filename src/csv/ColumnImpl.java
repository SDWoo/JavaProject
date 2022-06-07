package csv;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.regex.Pattern;

class ColumnImpl implements Column{
    private int index;
    private int notNullNumber;
    private int numberCount;
    private String type;
    private Class clazz;
    private String header;
    private int formatNum;
    private int countItem;
    private List<Double> doubles; // mean, std, max, min 을 위함
    List<String> Items;

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

    void setNotNullNumber(long count) {
        this.notNullNumber = Integer.valueOf((int) count);

    }

    public void setType(String type, Class clazz) {
        this.clazz = clazz;
        this.type = type;
    }

    List<String> getItems() {
        return Items;
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
    void makeCountZero() {
        this.countItem = 0;
    }

    @Override
    public String getValue(int index) {
        return Items.get(index);
    }

    @Override
    public <T extends Number> T getValue(int index, Class<T> t) {
        if(Items.get(index) != "") {
            if (t.getName() == "java.lang.Integer") {
                Integer item = Integer.parseInt(Items.get(index));
                return (T) item;
            } else if (t.getName() == "java.lang.Double") {
                Double item = Double.parseDouble(Items.get(index));
                return (T) item;
            } else {
                return null;
            }
        }
        else{
            return null;
        }
    }

    @Override
    public void setValue(int index, String value) {
        if (isEmptyOrNull(value)) {
        }
        else if (isInteger(value)) {
            notNullNumber += 1;
            numberCount += 1;
            formatNum = Math.max(formatNum, value.length());
            if (!type.equals("String") && !type.equals("double")) {
                type = "int";
                        clazz = Integer.class;
                    }
        } else if (isDouble(value)) {
            notNullNumber += 1;
            numberCount += 1;
            formatNum = Math.max(formatNum, value.length());
            if (!type.equals("String")) {
                type = "double";
                clazz = Double.class;
            }
        } else {
            notNullNumber += 1;
            formatNum = Math.max(formatNum, value.length());
            type = "String";
            clazz = String.class;
        }
//        try{
//            Items.get(index);
//        }catch (Exception e){
            if (index == this.getItems().size()) {
                Items.add(index, value);
            } else {
                Items.set(index, value);
            }
//        }

    }

    @Override
    public <T extends Number> void setValue(int index, T value) {
        if (value.getClass().isAssignableFrom(Double.class)) {
            Items.set(index, String.format("%.6f", value));
        } else {
            Items.set(index, String.format("%d",value));
        }
        this.formatNum = Math.max(formatNum, String.valueOf(value).length());

    }

    @Override
    public int count() {
        return notNullNumber;
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
        return this.Items.size() - this.notNullNumber;
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
        Double min = Items.stream()
                .filter(item -> !item.equals(""))
                .mapToDouble(item -> {
                    try {
                        return Double.valueOf(item);
                    } catch (Exception e) {
                        return Double.MAX_VALUE;
                    }
                })
                .min().getAsDouble();

        return min;

    }

    @Override
    public double getNumericMax() {
        Double max = Items.stream()
                .filter(item -> !item.equals(""))
                .mapToDouble(item -> {
                    try {
                        return Double.valueOf(item);
                    } catch (Exception e) {
                        return Double.MIN_VALUE;
                    }
                })
                .max().getAsDouble();

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
        Double mean = sum / numberCount;
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

    Class getClazz() {
        return clazz;
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
    boolean typeCheck(){
        boolean check = false;
        if(this.type == "double" || this.type == "int") {
            check = true;
        }
        return check;
    }

    @Override
    public boolean fillNullWithMean() {
        return typeCheck();
    }

    @Override
    public boolean fillNullWithZero() {
        return typeCheck();
    }

    @Override
    public boolean standardize() {
        return typeCheck();
    }

    @Override
    public boolean normalize() {
        return typeCheck();
    }

    @Override
    public boolean factorize() {
        return false;
    }
}
