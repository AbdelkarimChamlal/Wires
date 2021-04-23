package v3.data;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import v3.standards.Table;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * handles the output data
 */
public class ExportData {
    public static void exportTableToExcel(String fileName, String sheetName, Table table) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);
        ConvertData.fillSheetWithTable(sheet,table);
        FileOutputStream outputStream =  new FileOutputStream(fileName);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

    public static void appendTextToFile(String filePath,String text) throws IOException {
        Files.write(Paths.get(filePath), ("\n"+text).getBytes(), StandardOpenOption.APPEND);
    }
}
