package v3.models;

import v3.data.ConvertData;
import v3.interfaces.Table;
import v3.standards.Row;
import v3.utils.TemplateUtil;
import java.util.ArrayList;
import java.util.List;

public class MaxTable {
    Configs maxConfigs;
    Template maxTemplate;
    List<Row> rows;
    int headerPosition;
    List<MaxRow> maxRows;
    List<String> columns;

    public MaxTable(Configs maxConfigs,Template template, Table table) throws Exception {
        this.maxConfigs = maxConfigs;
        this.maxTemplate = template;
        this.rows = table.getRows();
        this.headerPosition = Integer.parseInt(this.maxConfigs.getConfigValue("headerPosition"));
        TemplateUtil.matchTableToTemplate(rows.get(headerPosition),maxTemplate);
        this.columns = this.rows.get(headerPosition).getValues();
        this.maxRows = convertRowsToMaxRows();
        maxRows.remove(headerPosition);
    }

    public List<MaxRow> getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(List<MaxRow> maxRows) {
        this.maxRows = maxRows;
    }

    public List<MaxRow> convertRowsToMaxRows(){
        List<MaxRow> maxRows = new ArrayList<>();
        for(Row row : rows){
            maxRows.add(ConvertData.convertRowIntoMaxRow(row,maxConfigs,columns));
        }
        return maxRows;
    }

    public Table convertToTable(){
        Table table = new v3.standards.Table();




        return table;
    }

    public List<MaxRow> rowsWithSameSourceInBothDirections(String source){
        List<MaxRow> maxRows = new ArrayList<>();
        for(MaxRow maxRow:this.getMaxRows()){
            if(maxRow.getToSource().equals(source) || maxRow.getFromSource().equals(source)){
                maxRows.add(maxRow);
            }
        }
        return maxRows;
    }

    public List<String> getColumns(){
        return this.columns;
    }








}
