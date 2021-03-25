package v1.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImportExcel {
    public static List<List<String>> importWorkSheet(String filePath,int sheetNum) throws IOException {
        InputStream fileStream = ImportExcel.class.getResourceAsStream(filePath);
        Workbook workbook = new XSSFWorkbook(fileStream);
        fileStream.close();
        Sheet sheet = workbook.getSheetAt(sheetNum);
        return ConvertToList.convertXlsxSheetUsingPOIToList(sheet);
    }

    public static List<List<String>> importWorkSheet(String filePath,String sheetName) throws IOException {
        InputStream fileStream = ImportExcel.class.getResourceAsStream(filePath);
        Workbook workbook = new XSSFWorkbook(fileStream);
        fileStream.close();
        Sheet sheet = workbook.getSheet(sheetName);
        return ConvertToList.convertXlsxSheetUsingPOIToList(sheet);
    }
}
