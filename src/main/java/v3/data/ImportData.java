package v3.data;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.*;

/**
 * Handles the input data
 */
public class ImportData {

    public static Sheet importSheet(String filePath,int sheetOrder) throws IOException {
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheetAt(sheetOrder);
        workbook.close();
        return sheet;
    }

    public static Sheet importSheet(String filePath,String sheetName) throws IOException {
        Workbook workbook = WorkbookFactory.create(new File(filePath));
        Sheet sheet = workbook.getSheet(sheetName);
        workbook.close();
        return sheet;
    }

    public static String importText(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) stringBuilder.append(st+"\n");
        return stringBuilder.toString();
    }

}
