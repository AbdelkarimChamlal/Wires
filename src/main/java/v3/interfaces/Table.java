package v3.interfaces;

import java.util.List;
import v3.standards.Row;

public interface Table {

    List<Row> getRows();
    void setRows(Table table);

}
