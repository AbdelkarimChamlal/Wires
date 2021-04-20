package v3.models;

import v3.data.ConvertData;
import v3.interfaces.Table;
import v3.primitiveModels.Row;
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
        if(!TemplateUtil.matchTableToTemplate(rows.get(headerPosition),maxTemplate)){
            throw new Exception("the provided table doesn't match the max template");
        }
        this.columns = this.rows.get(headerPosition).getValues();
        this.maxRows = convertRowsToMaxRows();
        maxRows.remove(headerPosition);
    }

    public List<MaxRow> convertRowsToMaxRows(){
        List<MaxRow> maxRows = new ArrayList<>();
        for(Row row : rows){
            maxRows.add(ConvertData.convertRowIntoMaxRow(row,maxConfigs,columns));
        }
        return maxRows;
    }

    public Table convertToTable(){
        Table table = new v3.primitiveModels.Table();




        return table;
    }






}
