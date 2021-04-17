package v3.models;

import java.util.HashMap;
import java.util.Map;

public class Row {
    Map<String,String> columnValue;

    public Row(){
        this.columnValue = new HashMap<>();
    }

    public String getValue(String column){
        return columnValue.get(column);
    }

    public void addValue(String column,String value){
        columnValue.put(column,value);
    }

    public boolean containsColumn(String column){
        return columnValue.containsKey(column);
    }

    public Map<String,String> getMap(){
        return this.columnValue;
    }
}
