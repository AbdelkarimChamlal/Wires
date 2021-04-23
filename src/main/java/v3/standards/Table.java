package v3.standards;

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

    public Row getRow(int rowPosition){
        return rows.get(rowPosition);
    }

    public void addRow(Row row){
        rows.add(row);
    }

    public void removeRow(int rowIndex){
        rows.remove(rowIndex);
    }

}
