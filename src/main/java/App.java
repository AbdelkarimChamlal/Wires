import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.HeaderGenerator;
import utils.OutputGenerator;
import utils.RowHandler;

import java.io.*;
import java.util.List;
import java.util.Map;

//todos
//TODO: the type of cell can cause problems in case of numeric and string do something about it.
//TODO: cleaning rows and make sure that a cell exist before using it
//TODO: document the code a little bit
//TODO: create private methods for doing small tasks and avoid big bois
//TODO: duplicated rows or duplicated rows with different values....
public class App {

    static String oldVersionPath = "/max.xlsx";
    static String newVersionPath = "/new max.xlsx";
    static String outputPath     = "C:\\Users\\Abdel\\OneDrive\\Desktop\\yazaki\\xlsx git\\src\\main\\resources\\max diffs.xlsx";

    static int oldVersionUniqueKey = 5;
    static int newVersionUniqueKey = 5;



    public static void main(String[] args) {
        //load old version book
        InputStream oldVersionStream = App.class.getResourceAsStream(oldVersionPath);
        Workbook oldVersionBook = null;
        try {
            oldVersionBook = new XSSFWorkbook(oldVersionStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            oldVersionStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load new version book
        InputStream newVersionStream = App.class.getResourceAsStream(newVersionPath);
        Workbook newVersionBook = null;
        try {
            newVersionBook = new XSSFWorkbook(newVersionStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            newVersionStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load oldVersion sheet
        assert oldVersionBook != null;
        Sheet oldVersionSheet = oldVersionBook.getSheetAt(0);

        //load newVersion sheet
        assert newVersionBook != null;
        Sheet newVersionSheet = newVersionBook.getSheetAt(0);

        //oldVersionHeader
        Row oldVersionHeader = oldVersionSheet.getRow(0);

        //newVersionHeader
        Row newVersionHeader = newVersionSheet.getRow(0);

        //get oldVersionHeaderValues
        List<String> oldVersionHeaderValues = HeaderGenerator.getHeaderValues(oldVersionHeader);

        //get newVersionHeaderValues
        List<String> newVersionHeaderValues = HeaderGenerator.getHeaderValues(newVersionHeader);

        //get Matching Values between both headers
        List<String> matchingValues = HeaderGenerator.getMatchingValues(oldVersionHeaderValues,newVersionHeaderValues);

        //get all different columns from both sheets
        List<String> allValues = HeaderGenerator.getAllValues(oldVersionHeaderValues,newVersionHeaderValues);

        //get positions of each matching value in the old and new version
        Map<Integer,Integer> matchingValuesPositions = HeaderGenerator.getMatchingValuesPositions(oldVersionHeaderValues,newVersionHeaderValues,matchingValues);

        //get all oldVersion rows
        List<Row> oldVersionRows = RowHandler.getAllRows(oldVersionSheet);
        System.out.println("old version size " + oldVersionRows.size());

        //get all newVersion rows
        List<Row> newVersionRows = RowHandler.getAllRows(newVersionSheet);
        System.out.println("new version size " + newVersionRows.size());

        //delete empty rows || column number < key position
        RowHandler.deleteEmptyRows(newVersionRows,newVersionUniqueKey);
        RowHandler.deleteEmptyRows(oldVersionRows,oldVersionUniqueKey);

        System.out.println("deleted empty rows in old version "+oldVersionRows.size());
        System.out.println("deleted empty rows in new version "+newVersionRows.size());


        //handle multiple rows with the same uniqueKey
        List<Row> oldVersionNoDuplicatedRows = RowHandler.handleMultipleRowsWithSameKey(oldVersionRows,oldVersionUniqueKey,RowHandler.IN_CASE_MORE_THAN_ONE_ROW_WITH_SAME_UNIQUE_KEY_KEEP_FIRST);
        List<Row> newVersionNoDuplicatedRows = RowHandler.handleMultipleRowsWithSameKey(newVersionRows,newVersionUniqueKey,RowHandler.IN_CASE_MORE_THAN_ONE_ROW_WITH_SAME_UNIQUE_KEY_KEEP_FIRST);

        System.out.println("old version size with no duplication " + oldVersionNoDuplicatedRows.size());
        System.out.println("new version size with no duplication " + newVersionNoDuplicatedRows.size());


        //check for deleted rows
        List<Row> deletedRows = RowHandler.getDeletedRows(oldVersionNoDuplicatedRows,newVersionNoDuplicatedRows,oldVersionUniqueKey,newVersionUniqueKey);
        System.out.println("how many deleted "+deletedRows.size());

        //check for added rows
        List<Row> addedRow = RowHandler.getAddedRows(oldVersionNoDuplicatedRows,newVersionNoDuplicatedRows,oldVersionUniqueKey,newVersionUniqueKey);
        System.out.println("how many added "+addedRow.size());

        //check for matching rows
        List<List<Row>> matchingRows = RowHandler.getMatchingRows(oldVersionNoDuplicatedRows,newVersionNoDuplicatedRows,oldVersionUniqueKey,newVersionUniqueKey);
        System.out.println("how many matching "+matchingRows.size());

        //get nonModified Rows
        List<List<Row>> nonModifiedRows = RowHandler.getNonModifiedRows(matchingRows,matchingValuesPositions);
        System.out.println("non modified rows "+nonModifiedRows.size());

        //get modified rows
        List<List<Row>> modifiedRows = RowHandler.getModifiedRows(matchingRows,matchingValuesPositions);
        System.out.println("modified rows "+modifiedRows.size());

        //now its time to start collecting all data together and form it into a new sheet with all changes

        //create new workBook
        Workbook outputBook = new XSSFWorkbook();

        //create the output sheet and name it tickets
        Sheet outputSheet = outputBook.createSheet("changes");

        OutputGenerator.createHeader(allValues,outputSheet);

        int totalRowsInOutput = newVersionNoDuplicatedRows.size() + deletedRows.size();

        for(int i = 1; i<= totalRowsInOutput ; i++){
            //start with deleted ones
            if(i<=deletedRows.size()){
                OutputGenerator.updateRow(allValues,oldVersionHeaderValues,outputSheet,deletedRows.get(i-1),"DELETED",i);
            }else
            //added rows
            if(i<=deletedRows.size()+addedRow.size()){
                OutputGenerator.updateRow(allValues,newVersionHeaderValues,outputSheet,addedRow.get(i-deletedRows.size()-1),"ADDED",i);
            }else
            //add non modified rows
            if(i<=deletedRows.size() + addedRow.size() + nonModifiedRows.size()){
                Row outputRow = OutputGenerator.createFinalRow(allValues,outputSheet,i);
                OutputGenerator.updateRowForMatching(outputRow,allValues,oldVersionHeaderValues,nonModifiedRows.get(i-deletedRows.size()-addedRow.size()-1).get(0),"NOT MODIFIED");
                OutputGenerator.updateRowForMatching(outputRow,allValues,newVersionHeaderValues,nonModifiedRows.get(i-deletedRows.size()-addedRow.size()-1).get(1),"NOT MODIFIED");
            }else
            //add modified rows
            if(i<=deletedRows.size() + addedRow.size() + nonModifiedRows.size()+modifiedRows.size()){
                Row outputRow = OutputGenerator.createFinalRow(allValues,outputSheet,i);
                OutputGenerator.updateRowForMatching(outputRow,allValues,oldVersionHeaderValues,modifiedRows.get(i-deletedRows.size()-addedRow.size()-nonModifiedRows.size()-1).get(0),"MODIFIED");
                OutputGenerator.updateRowForMatching(outputRow,allValues,newVersionHeaderValues,modifiedRows.get(i-deletedRows.size()-addedRow.size()-nonModifiedRows.size()-1).get(1),"MODIFIED");
            }
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outputPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            outputBook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert outputStream != null;
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
