package v3.utils;

import v3.primitiveModels.Row;

public class RowUtil {
    public static Row duplicateRow(Row row){
        Row duplication = new Row();
        for (String column:row.getValues()){
            duplication.addValue(column);
        }
        return duplication;
    }

    public static Row emptyRow(int size){
        Row row = new Row();
        for(int i = 0 ; i < size ; i ++){
            row.getValues().add("");
        }
        return row;
    }

    public static boolean isDuplicated(Row row1,Row row2){
        for(int i = 0 ; i < row1.getValues().size() ; i++){
            if(!row1.getValue(i).equals(row2.getValue(i)))return false;
        }
        return true;
    }
}
