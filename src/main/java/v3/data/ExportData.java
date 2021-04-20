package v3.data;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import v2.utils.Convertor;
import v3.primitiveModels.Table;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
}
