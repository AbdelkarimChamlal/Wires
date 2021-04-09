package v2.utils;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Export data from a Java table "List<List<String>>" into a specific output file
 */
public class ExportData {

    /**
     * exports java table as an excel file.
     *
     * @param workbookName the name of the output file
     * @param sheetName name of the sheet in side the excel file
     * @param table the data
     * @throws IOException when failed to write the workbook to the disk
     */
    public static void exportTableToExcel(String fileName, String sheetName, List<List<String>> table) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        Convertor.convertTableIntoSheet(sheet,table);
        FileOutputStream outputStream =  new FileOutputStream(fileName);
        workbook.write(outputStream);
        outputStream.close();
    }

    /**
     * convert java 2D list into a sheet with a coloring
     * <p>option for cells which contains certain symbol values that are defined in Values class.
     *
     * @param fileName the output path and name
     * @param sheetName the output sheet name
     * @param table the java 2D list which contains values
     * @throws IOException in case of IO problems
     */
    public static void exportTableToExcelWithModifiedCellsColored(String fileName, String sheetName, List<List<String>> table) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        Convertor.convertTableIntoSheetWithModifiedCellsColored(workbook,sheet,table);
        FileOutputStream outputStream =  new FileOutputStream(fileName);
        workbook.write(outputStream);
        outputStream.close();
    }
}
