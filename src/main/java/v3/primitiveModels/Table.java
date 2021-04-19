package v3.abstracts;

import v3.abstracts.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * a 2D list which holds data
 */
public class Table implements v3.interfaces.Table {
    List<Row> rows;

    public List<Row> getRows() {
        return rows;
    }

    @Override
    public void setRows(v3.interfaces.Table table) {
        this.rows = table.getRows();
    }

    public Table(){
        this.rows = new ArrayList<>();
    }
}
