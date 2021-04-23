package v3.standards;

import java.util.ArrayList;
import java.util.List;

public class Row implements v3.interfaces.Row {
    List<String> values;

    public Row(){
        this.values = new ArrayList<>();
    }

    public Row(int size){
        this.values = new ArrayList<>(size);
    }

    public String getValue(int columnPosition){
        return values.get(columnPosition);
    }

    public void addValue(String column){
        values.add(column);
    }

    public void setValues(List<String> values){
        this.values = values;
    }

    public boolean containsValue(String value){
        return values.contains(value);
    }

    public List<String> getValues(){
        return this.values;
    }

    @Override
    public String toString(){
        return this.values.toString();
    }
}
