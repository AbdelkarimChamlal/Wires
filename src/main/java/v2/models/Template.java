package v2.models;

import java.util.List;
import java.util.Map;

public class Template {
    List<String> columns;
    Map<String,Integer> columnPositions;

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
}
