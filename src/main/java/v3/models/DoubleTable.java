package v3.models;

import v3.data.ConvertData;
import v3.interfaces.Table;
import v3.primitiveModels.Row;
import v3.utils.TemplateUtil;

import java.util.ArrayList;
import java.util.List;

public class DoubleTable {
    Configs crimpingConfigs;
    Template crimpingTemplate;
    int headerPosition;
    List<Row> rows;
    List<DoubleRow> doubleRows;
    List<String> columns;

    public DoubleTable(Configs crimpingConfigs, Template crimpingTemplate, Table table) throws Exception {
        this.crimpingConfigs = crimpingConfigs;
        this.crimpingTemplate = crimpingTemplate;
        this.rows = table.getRows();
        this.headerPosition = Integer.parseInt(this.crimpingConfigs.getConfigValue("headerPosition"));
        if(!TemplateUtil.matchTableToTemplate(rows.get(headerPosition),crimpingTemplate)){
            throw new Exception("the provided table doesn't match the max template");
        }
        this.columns = this.rows.get(headerPosition).getValues();
        this.doubleRows = convertRowsToDoubleRows();
    }


    public List<DoubleRow> convertRowsToDoubleRows(){
        List<DoubleRow> doubleRows = new ArrayList<>();
        for(Row row : rows){
            doubleRows.add(ConvertData.convertRowIntoDoubleRow(row,crimpingConfigs,columns));
        }
        return doubleRows;
    }



}
