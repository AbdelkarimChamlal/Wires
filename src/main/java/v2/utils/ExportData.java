package v2.utils;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
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

    public static void fillSheetWithTable(Sheet sheet,List<List<String>> table){
        for(int i = 1 ; i < table.size() ; i++){
            Row row = sheet.createRow(i);
            for(int j = 0 ; j < table.get(0).size() ; j++){
                Cell cell = row.createCell(j);
                cell.setCellValue(table.get(i).get(j));
            }
        }
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

    public static void exportCuttingTable(String fileName,List<List<String>> wireList,List<List<String>> doubles,List<List<String>> twists,List<List<String>> splices) throws IOException {
        File file = new File("templates/cuttingData.xlsx");
        Workbook workbook = WorkbookFactory.create(file);
        Sheet wireListSheet = workbook.getSheet("wire list");
        Sheet doublesSheet = workbook.getSheet("doubles");
        Sheet twistsSheet = workbook.getSheet("twists");
        Sheet splicesSheet = workbook.getSheet("splices");

        Convertor.addToSheet(wireListSheet,wireList);
        Convertor.addToSheet(doublesSheet,doubles);
        Convertor.addToSheet(twistsSheet,twists);
        Convertor.addToSheet(splicesSheet,splices);

        FileOutputStream outputStream =  new FileOutputStream(fileName);
        workbook.write(outputStream);
        outputStream.close();
    }

    public static void exportTableUsingTemplate(String fileName, List<List<String>> table,String templateName) throws IOException {
        Workbook workbook = WorkbookFactory.create(new File("templates/"+templateName));
        Sheet sheet = workbook.getSheetAt(0);
        fillSheetWithTable(sheet,TemplateUtil.convertToTemplate(table,templateName));
        FileOutputStream outputStream =  new FileOutputStream(fileName);
        workbook.write(outputStream);
        outputStream.close();
    }
}
