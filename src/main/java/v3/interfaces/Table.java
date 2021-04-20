package v3.interfaces;

import java.util.List;
import v3.primitiveModels.Row;

public interface Table {

    public List<Row> getRows();
    public void setRows(Table table);

}
