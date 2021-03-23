package utils;

import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class DoubleGenerator {
    public static List<List<Row>> hasDouble(List<List<Row>> rowsWithKeys,int wireTypeIndex){
        List<List<Row>> rowsWithDouble = new ArrayList<>();
        for (List<Row> rows : rowsWithKeys){
            List<Row> rowsWithSameKey = new ArrayList<>();
            for(Row row:rows){
                if(row.getCell(wireTypeIndex).getStringCellValue().toLowerCase().contains("double")){
                    rowsWithSameKey.add(row);
                }
            }
            rowsWithDouble.add(rowsWithSameKey);
        }
        return rowsWithDouble;
    }

    public static List<String> commonKeys(List<String> stringList1, List<String> stringList2){
        List<String> commonKeys = new ArrayList<>();

        for(String key1:stringList1){
            for (String key2:stringList2){
                if(key1.equals(key2)){
                    commonKeys.add(key1);
                }
            }
        }

        return commonKeys;
    }

    public static List<List<Row>> deleteUnCommonKeysAndRows(List<String> commonKeys,List<String> keys,List<List<Row>> rows){
        List<List<Row>> commonRows = new ArrayList<>();

        for(int i =0; i<keys.size(); i++){
            if(commonKeys.contains(keys.get(i))){
                commonRows.add(rows.get(i));
            }
        }

        return commonRows;
    }
}
