package v3.utils;

import v3.data.ConvertData;
import v3.data.ImportData;
import v3.models.Configs;
import v3.standards.Row;
import v3.standards.Table;

import java.util.ArrayList;
import java.util.List;

public class TableUtil {
    public static void addColumn(Table table,String columnName){
        int position = 0;
        for (Row row: table.getRows()){
            row.getValues().add((position==0)?columnName:"");
            position++;
        }
    }

    public static void addColumn(Table table,String columnName,int columnPosition){
        int position = 0;
        for (Row row: table.getRows()){
            row.getValues().add(columnPosition,(position==0)?columnName:"");
            position++;
        }
    }

    public static Table getRowsWithCommonColumnValue(Table table,String commonValue,int columnPosition){
        Table subTable = new Table();
        for(Row row:table.getRows()){
            if(row.getValue(columnPosition).equals(commonValue)){
                subTable.getRows().add(RowUtil.duplicateRow(row));
            }
        }
        return subTable;
    }

    public static Row getRowById(Table table,String id, int idPosition){
        for(Row row: table.getRows()){
            if(row.getValue(idPosition).equals(id))return RowUtil.duplicateRow(row);
        }
        return null;
    }

    public static List<String> getUniqueValuesForAColumn(Table table,int columnPosition){
        List<String> uniqueValues = new ArrayList<>();
        for(Row row : table.getRows()){
            if(!uniqueValues.contains(row.getValue(columnPosition)))uniqueValues.add(row.getValue(columnPosition));
        }
        return uniqueValues;
    }

    public static void removeDuplicatedRows(Table table){
        for(int i = 0 ; i < table.getRows().size() ; i++){
            for(int j = i + 1 ; j < table.getRows().size() ; j++){
                if(RowUtil.isDuplicated(table.getRow(i),table.getRow(j))){
                    table.getRows().remove(j);
                    j--;
                }
            }
        }
    }

    public static void removeColumn(v3.interfaces.Table table, int columnPosition){
        for(int i = 0 ; i < table.getRows().size() ; i++){
            table.getRows().get(i).getValues().remove(columnPosition);
        }
    }

    public static void removeRow(v3.interfaces.Table table, int rowIndex){
        table.getRows().remove(rowIndex);
    }

    public static Table duplicateTable(Table table){
        Table duplicatedTable = new Table();
        for(Row row:table.getRows()){
            duplicatedTable.getRows().add(RowUtil.duplicateRow(row));
        }
        return duplicatedTable;
    }

    public static Table importTableUsingConfigurations(String filePath, Configs configs,String tableName) throws Exception {
        try{
            Table table;
            if(configs.getConfigValue("importBy").equals("ORDER")){
                table = ConvertData.convertSheetIntoTable(ImportData.importSheet(filePath,Integer.parseInt(configs.getConfigValue("importByValue"))));
            }else{
                table = ConvertData.convertSheetIntoTable(ImportData.importSheet(filePath,configs.getConfigValue("importByValue")));
            }
            return table;
        }catch(Exception e){
            throw new Exception("FAILED TO EXTRACT "+tableName+" SHEET FOLLOWING THE CONFIGURATIONS");
        }
    }

}
