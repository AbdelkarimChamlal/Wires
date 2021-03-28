package v2.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Convert data from one format to other
 */
public class Convertor {
    /**
     * convert Sheet object into java 2D table
     * @param sheet the sheet object
     * @return 2D java table
     */
    public static List<List<String>> convertXlsxSheetUsingPOIToList(Sheet sheet){
        List<List<String>> convertedList = new ArrayList<>();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while(rowIterator.hasNext()){
            List<String> values = new ArrayList<>();
            Row row = rowIterator.next();
            for(int i =0;i<row.getLastCellNum() ; i++){
                try{
                    Cell cell = row.getCell(i);
                    if(cell.getCellType()==CellType.NUMERIC){
                        values.add(String.valueOf((long) cell.getNumericCellValue()));
                    }else{
                        values.add(cell.getStringCellValue());
                    }
                }catch(Exception e){
                    values.add("");
                }
            }
            convertedList.add(values);
        }
        return convertedList;
    }


    /**
     * convert a java table "List" and write it into a sheet which is taken as input.
     *
     * @param sheet the sheet object which the table will be written to
     * @param table the table which contains the data
     * @return the same sheet input after modifications
     */
    public static Sheet convertTableIntoSheet(Sheet sheet,List<List<String>> table){
        for(int i = 0 ; i<table.size() ; i++){
            Row row = sheet.createRow(i);
            for(int j=0; j<table.get(i).size() ; j++){
                Cell cell = row.createCell(j);
                cell.setCellValue(table.get(i).get(j));
            }
        }
        return sheet;
    }

}
