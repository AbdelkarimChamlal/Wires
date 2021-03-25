package v1.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConvertToList {
    //TODO: properly convert to string when CellType!=STRING


    public static List<List<String>> convertXlsxSheetUsingPOIToList(Sheet sheet){
            List<List<String>> convertedList = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.rowIterator();
            while(rowIterator.hasNext()){
                List<String> values = new ArrayList<>();
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while(cellIterator.hasNext()){
                    Cell cell = cellIterator.next();
                    if(cell!=null){
                        if(cell.getCellType() == CellType.NUMERIC){
                            values.add(""+cell.getNumericCellValue());
                        }else{
                            values.add(cell.getStringCellValue());
                        }
                    }else{
                        values.add("");
                    }
                }
                convertedList.add(values);
            }
        return convertedList;
    }


}
