package v1.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

public class RowHandler {

    public final static int IN_CASE_MORE_THAN_ONE_ROW_WITH_SAME_UNIQUE_KEY_KEEP_FIRST = 1;
    public final static int IN_CASE_MORE_THAN_ONE_ROW_WITH_SAME_UNIQUE_KEY_KEEP_LAST = 2;

    public static List<Row> getAllRows(Sheet sheet){
        List<Row> rows = new ArrayList<>();

        Iterator<Row> rowIterator = sheet.rowIterator();
        while(rowIterator.hasNext()){
            rows.add(rowIterator.next());
        }
        rows.remove(0);
        return rows;
    }

    public static List<Cell> getCellsFromRow(Row row){
        List<Cell> cells = new ArrayList<>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while(cellIterator.hasNext()){
            cells.add(cellIterator.next());
        }

        return cells;
    }

    public static List<Row> handleMultipleRowsWithSameKey(List<Row> allRows,int keyPosition,int handleProtocol){
        List<Row> result = new ArrayList<>();

        if(handleProtocol== RowHandler.IN_CASE_MORE_THAN_ONE_ROW_WITH_SAME_UNIQUE_KEY_KEEP_FIRST){
            List<String> keys = new ArrayList<>();

            for(Row row : allRows){

                List<Cell> cells = getCellsFromRow(row);
                    if(cells.get(keyPosition).getCellType()!= CellType.STRING){
                        cells.get(keyPosition).setCellType(CellType.STRING);
                    }
                    if(!keys.contains(cells.get(keyPosition).getStringCellValue())){
                        keys.add(cells.get(keyPosition).getStringCellValue());
                        result.add(row);
                    }
                }

        }


        return result;
    }

    public static List<Row> getDeletedRows(List<Row> oldRows, List<Row> newRows,int oldKey,int newKey){
        List<Row> deletedRows = new ArrayList<>();

        for(int i =0 ; i <oldRows.size(); i++){
            boolean match = false;
            for(int j=0 ; j<newRows.size();j++){

                if(oldRows.get(i).getCell(oldKey).getStringCellValue().equals(newRows.get(j).getCell(newKey).getStringCellValue())){
                    match = true;
                    break;

                }
            }
            if(!match){
                deletedRows.add(oldRows.get(i));
            }
        }

        return deletedRows;
    }

    public static void deleteEmptyRows(List<Row> rows,int keyPosition){
        for(int i =0 ; i< rows.size() ;i++){

            Row row = rows.get(i);

            List<Cell> cells = getCellsFromRow(row);
            if(cells.size()<keyPosition){
                rows.remove(i);
                i--;
            }
        }
    }

    public static List<Row> getAddedRows(List<Row> oldRows,List<Row> newRows,int oldKey,int newKey){
        List<Row> added = new ArrayList<>();

        for(int i =0 ; i< newRows.size() ; i++){
            String key = newRows.get(i).getCell(newKey).getStringCellValue();
            boolean foundMatch = false;
            for(int j =0; j<oldRows.size();j++){
                if(oldRows.get(j).getCell(oldKey).getStringCellValue().equals(key)){
                    foundMatch=true;
                    break;
                }
            }

            if(!foundMatch){
                added.add(newRows.get(i));
            }
        }

        return added;
    }

    public static List<List<Row>> getMatchingRows(List<Row> oldRows,List<Row> newRows , int oldKey,int newKey){
        List<List<Row>> matchingRows = new ArrayList<>();

        for (Row oldRow : oldRows) {
            List<Row> matchingRow = new ArrayList<>();
            String old_key = oldRow.getCell(oldKey).getStringCellValue();

            for (Row newRow : newRows) {
                String new_key = newRow.getCell(newKey).getStringCellValue();
                if (new_key.equals(old_key)) {
                    matchingRow.add(oldRow);
                    matchingRow.add(newRow);
                    matchingRows.add(matchingRow);
                    break;
                }
            }
        }

        return matchingRows;
    }

    public static List<List<Row>> getNonModifiedRows(List<List<Row>> matchingRows, Map<Integer,Integer> matchingValuesPositions ){
        List<List<Row>> nonModifiedRows = new ArrayList<>();
        Set keySet = matchingValuesPositions.keySet();

        for(List<Row> matchingRow : matchingRows){
            boolean modified = false;

            for (Object o : keySet) {
                int key = (Integer) o;
                if(matchingRow.get(0).getCell(key).getCellType()== CellType.NUMERIC && matchingRow.get(1).getCell(matchingValuesPositions.get(key)).getCellType() == CellType.NUMERIC){

                    if(matchingRow.get(0).getCell(key).getNumericCellValue() != matchingRow.get(1).getCell(matchingValuesPositions.get(key)).getNumericCellValue()){
                        modified = true;
                        break;
                    }

                }else{
                    matchingRow.get(0).getCell(key).setCellType(CellType.STRING);
                    matchingRow.get(1).getCell(matchingValuesPositions.get(key)).setCellType(CellType.STRING);
                    if(!matchingRow.get(0).getCell(key).getStringCellValue().equalsIgnoreCase(matchingRow.get(1).getCell(matchingValuesPositions.get(key)).getStringCellValue())){
                        modified = true;
                        break;
                    }
                }
            }

            if(!modified){
                nonModifiedRows.add(matchingRow);
            }
        }

        return nonModifiedRows;
    }

    public static List<List<Row>> getModifiedRows(List<List<Row>> matchingRows, Map<Integer,Integer> matchingValuesPositions){
        List<List<Row>> modifiedRows = new ArrayList<>();
        Set keySet = matchingValuesPositions.keySet();

        for(List<Row> matchingRow : matchingRows){
            boolean modified = false;

            for (Object o : keySet) {
                int key = (Integer) o;

                if(matchingRow.get(0).getCell(key).getCellType()== CellType.NUMERIC && matchingRow.get(1).getCell(matchingValuesPositions.get(key)).getCellType() == CellType.NUMERIC){

                    if(matchingRow.get(0).getCell(key).getNumericCellValue() != matchingRow.get(1).getCell(matchingValuesPositions.get(key)).getNumericCellValue()){
                        modified = true;
                        break;
                    }

                }else{
                    matchingRow.get(0).getCell(key).setCellType(CellType.STRING);
                    matchingRow.get(1).getCell(matchingValuesPositions.get(key)).setCellType(CellType.STRING);
                    if(!matchingRow.get(0).getCell(key).getStringCellValue().equalsIgnoreCase(matchingRow.get(1).getCell(matchingValuesPositions.get(key)).getStringCellValue())){
                        modified = true;
                        break;
                    }
                }
            }

            if(modified){
                modifiedRows.add(matchingRow);
            }
        }

        return modifiedRows;
    }

    public static List<String> getDifferentValues(List<Row> allRows,int key){
        List<String> differentKeys = new ArrayList<>();

        for(Row row : allRows){
            String value = row.getCell(key).getStringCellValue();
            if(!differentKeys.contains(value)){
                differentKeys.add(value);
            }
        }

        return differentKeys;
    }

    public static List<List<Row>> getRowsWithSameKey(List<Row> allRows,List<String> differentKeys,int uniqueKey){
        List<List<Row>> rows = new ArrayList<>();
        for(String key : differentKeys){
            List<Row> rowsWithSameKey = new ArrayList<>();
            for(Row row : allRows){
                if(row.getCell(uniqueKey).getStringCellValue().equalsIgnoreCase(key)){
                    rowsWithSameKey.add(row);
                }
            }
            rows.add(rowsWithSameKey);
        }
        return rows;
    }

    public static boolean checkForSameValues(Row row1,Row row2,int pos1,int pos2){
        return (row1.getCell(pos1).getStringCellValue().toLowerCase().contains(row2.getCell(pos2).getStringCellValue().toLowerCase()) || row1.getCell(pos2).getStringCellValue().toLowerCase().contains(row1.getCell(pos1).getStringCellValue().toLowerCase()));
    }
}
