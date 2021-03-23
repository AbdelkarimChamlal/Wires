package models;

import org.apache.poi.ss.usermodel.Row;

public class RowReport {
    Row row;
    String statue;

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public String getStatue() {
        return statue;
    }

    public void setStatue(String statue) {
        this.statue = statue;
    }

    public RowReport(Row row, String statue) {
        this.row = row;
        this.statue = statue;
    }

    public RowReport(){

    }
}
