package v3.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Template is just the header of an input file, which contains the column values and their positions.
 */
public class Template {
    List<String> columns;
    Map<String,Integer> columnPositions;

    public Template(){
        this.columns = new ArrayList<>();
        this.columnPositions = new HashMap<>();
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public Map<String, Integer> getColumnPositions() {
        return columnPositions;
    }

    public void setColumnPositions(Map<String, Integer> columnPositions) {
        this.columnPositions = columnPositions;
    }

    public void putColumnPosition(String key,int position){
        this.columnPositions.put(key,position);
    }

    public int getColumnPosition(String key){
        return columnPositions.get(key);
    }

    public boolean containsColumn(String key){
        return columns.contains(key);
    }

    public void addColumn(String column){
        columns.add(column);
    }
}
