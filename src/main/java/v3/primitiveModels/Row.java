package v3.abstracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row implements v3.interfaces.Row {
    List<String> values;

    public Row(){
        this.values = new ArrayList<>();
    }

    public String getValue(int columnPosition){
        return values.get(columnPosition);
    }

    public void addValue(String column){
        values.add(column);
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
