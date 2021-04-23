package v3.models;

import v3.data.ConvertData;
import v3.interfaces.Table;
import v3.standards.Row;
import v3.utils.TemplateUtil;

import java.util.ArrayList;
import java.util.List;

public class CrimpingTable {
    Configs crimpingConfigs;
    Template crimpingTemplate;
    int headerPosition;
    List<Row> rows;
    List<CrimpingRow> crimpingRows;
    List<String> columns;

    public CrimpingTable(Configs crimpingConfigs, Template crimpingTemplate, Table table) throws Exception {
        this.crimpingConfigs = crimpingConfigs;
        this.crimpingTemplate = crimpingTemplate;
        this.rows = table.getRows();
        this.headerPosition = Integer.parseInt(this.crimpingConfigs.getConfigValue("headerPosition"));
        if(!TemplateUtil.matchTableToTemplate(rows.get(headerPosition),crimpingTemplate)){
            throw new Exception("the provided table doesn't match the max template");
        }
        this.columns = this.rows.get(headerPosition).getValues();
        this.crimpingRows = convertRowsToDoubleRows();
    }


    public List<CrimpingRow> convertRowsToDoubleRows(){
        List<CrimpingRow> crimpingRows = new ArrayList<>();
        for(Row row : rows){
            crimpingRows.add(ConvertData.convertRowIntoDoubleRow(row,crimpingConfigs,columns));
        }
        return crimpingRows;
    }



}
