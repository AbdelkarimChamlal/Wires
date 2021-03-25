package v1.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public class OutputGenerator {

    public static Row createFinalRow(List<String> outputColumns, Sheet outputSheet, int rowPosition){

        Row outputRow = outputSheet.createRow(rowPosition);

        for(int i = 0 ; i < outputColumns.size() ; i++){
            outputRow.createCell(i);
        }

        //this last one explains the change ADDED - DELETED - MODIFIED - NOTHING
        outputRow.createCell(outputColumns.size());

        return outputRow;
    }

    public static Row createHeader(List<String> outputColumns,Sheet outputSheet){
        Row header = outputSheet.createRow(0);

        for(int i = 0 ; i < outputColumns.size() ; i++){
            Cell column = header.createCell(i);
            column.setCellValue(outputColumns.get(i));
        }

        //this last one explains the change ADDED - DELETED - MODIFIED - NOTHING
        Cell reportColumn = header.createCell(outputColumns.size());
        reportColumn.setCellValue("Changes Type");
        return header;
    }

    public static void updateRow(List<String> allColumns,List<String> rowColumns,Sheet outputSheet, Row rowValues,String changeType,int rowPosition){
        Row newRow = createFinalRow(allColumns,outputSheet,rowPosition);

        for(int i=0 ; i<allColumns.size();i++){
            newRow.getCell(i).setCellValue("-");
            for(int j=0;j<rowColumns.size();j++){
                if (allColumns.get(i).equalsIgnoreCase(rowColumns.get(j))){
                    rowValues.getCell(j).setCellType(CellType.STRING);
                    newRow.getCell(i).setCellValue(rowValues.getCell(j).getStringCellValue());
                }
            }
        }

        newRow.getCell(allColumns.size()).setCellValue(changeType);
    }

    public static void updateRowForMatching(Row outputRow,List<String> allColumns,List<String> rowColumns, Row rowValues,String changeType){
        for(int i=0 ; i<allColumns.size();i++){
            for(int j=0;j<rowColumns.size();j++){
                if (allColumns.get(i).equalsIgnoreCase(rowColumns.get(j))){
                    rowValues.getCell(j).setCellType(CellType.STRING);
                    outputRow.getCell(i).setCellValue(rowValues.getCell(j).getStringCellValue());
                }
            }
        }
        outputRow.getCell(allColumns.size()).setCellValue(changeType);
    }


}
