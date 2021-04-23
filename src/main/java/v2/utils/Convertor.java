package v2.utils;

import org.apache.poi.ss.usermodel.*;
import v2.helpers.Values;
import v2.models.Revision;

import java.util.*;

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

    /**
     * convert java 2D list into a sheet and checks if a cell contains
     * <p></p>the modified symbol then it changes the background of this cell to indicate that it were modified.
     *
     * @param workbook Workbook instant of the output
     * @param sheet The sheet we want to put convert table into
     * @param table Java 2d List which will be converted
     * @return the input sheet after adding the table values into it.
     */

    public static Sheet convertTableIntoSheetWithModifiedCellsColored(Workbook workbook,Sheet sheet,List<List<String>> table){
        for(int i = 0 ; i<table.size() ; i++){
            Row row = sheet.createRow(i);
            for(int j=0; j<table.get(i).size() ; j++){
                Cell cell = row.createCell(j);
                String value = table.get(i).get(j);
                if(value.contains(Values.MODIFIED_SYMBOL)){
                    value = value.replace(Values.MODIFIED_SYMBOL,"");
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());
                    cellStyle.setFillBackgroundColor(IndexedColors.YELLOW.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellValue(value);
            }
        }
        return sheet;
    }

    public static Sheet addToSheet(Sheet sheet,List<List<String>> table){
        for(int i = 0 ; i<table.size() ; i++){
            Row row = sheet.createRow(i+1);
            for(int j=0; j<table.get(i).size() ; j++){
                Cell cell = row.createCell(j);
                cell.setCellValue(table.get(i).get(j));
            }
        }
        return sheet;
    }

    public static Map<String, Revision> convertStringToRevisions(String text){
        Map<String,Revision> revisionMap = new HashMap<>();
        String[] revisions = text.split("\n");
        for(int i = 0 ; i < revisions.length; i++){
            if(!revisions[i].startsWith("#") && revisions[i].length()>2){

                String[] values = revisions[i].split("::");
                for(int j = 0 ; j < values.length ; j++){
                    if(values[j].startsWith("\"") && values[j].endsWith("\"")){
                        values[j] = values[j].substring(1,values[j].length()-1);
                    }
                }

                Revision revision = new Revision();

                revision.setWirePM(values[1]);
                revision.setTwistSK(values[2]);
                revision.setDoublePM(values[3]);
                revision.setDoubleSK(values[4]);
                revision.setTwistPM(values[5]);
                revision.setWireSK(values[6]);

                revisionMap.put(values[0],revision);
            }
        }
        return revisionMap;
    }

    public static String convertToRevisionText(String hash,Revision revision){
        return "\""+hash+"\""+"::"+"\""+revision.getWirePM()+"\""+"::"+"\""+revision.getWireSK()+"\""+"::"+"\""+revision.getDoublePM()+"\""+"::"+"\""+revision.getDoubleSK()+"\""+"::"+"\""+revision.getTwistPM()+"\""+"::"+"\""+revision.getTwistSK()+"\"";
    }


}
