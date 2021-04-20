package v3.utils;

import v3.primitiveModels.Row;
import v3.primitiveModels.Table;

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

}
