package v3.models;

import java.util.ArrayList;
import java.util.List;

/**
 * a 2D list which holds data
 */
public class Table {
    List<List<String>> rows;

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> table) {
        this.rows = table;
    }

    public Table(){
        this.rows = new ArrayList<>();
    }
}
