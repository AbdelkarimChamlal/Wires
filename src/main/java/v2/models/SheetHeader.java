package v2.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintain the header values and their position
 */
public class SheetHeader {
    List<String> columnValues;
    List<Integer> columnPosition;

    public List<String> getColumnValues() {
        return columnValues;
    }

    public void setColumnValues(List<String> columnValues) {
        this.columnValues = columnValues;
    }

    public SheetHeader() {
        this.columnPosition = new ArrayList<>();
        this.columnValues = new ArrayList<>();
    }

    public SheetHeader(List<String> columnValues, List<Integer> columnPosition) {
        this.columnValues = columnValues;
        this.columnPosition = columnPosition;
    }

    public List<Integer> getColumnPosition() {
        return columnPosition;
    }

    public void setColumnPosition(List<Integer> columnPosition) {
        this.columnPosition = columnPosition;
    }
}
