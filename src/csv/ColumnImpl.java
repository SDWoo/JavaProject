package csv;
import java.util.ArrayList;
import java.util.List;

class ColumnImpl implements Column{
    private int index;
    private int notNullNumber;
    private String type;
    private String header;
    private List<String> Items;

    ColumnImpl(String header){
        this.header = header;
        this.type = "";
        this.index = 0;
        this.notNullNumber = 0;
        Items = new ArrayList<String>();
    }
    ColumnImpl(){
        this.header = null;
        this.type = "";
        this.index = 0;
        this.notNullNumber = 0;
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
        if(value.isEmpty()){

        }
        else if(isInteger(value)){
            notNullNumber += 1;
            if (!type.equals("String") && !type.equals("double")) {
                type = "int";
            }
        }
        else if(isDouble(value)){
            notNullNumber += 1;
            if (!type.equals("String")) {
                type = "double";
            }
        }else {
            notNullNumber += 1;
            type = "String";
        }
        Items.add(index, value);
    }

    @Override
    public <T extends Number> void setValue(int index, T value) {

    }

    @Override
    public int count() {
        return Items.size();
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
    String getType() {
        return type;
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
