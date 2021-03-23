import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.HeaderGenerator;
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
    static String newVersionPath = "/min.xlsx";

    static int oldVersionUniqueKey = 6;
    static int newVersionUniqueKey = 4;



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
        System.out.println(" modified rows "+modifiedRows.size());
    }
}
