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
        return WorkbookFactory.create(new File(filePath)).getSheetAt(sheetOrder);
    }
    public static Sheet importSheet(String filePath,String sheetName) throws IOException {
        return WorkbookFactory.create(new File(filePath)).getSheet(sheetName);
    }

    public static String importText(String filePath) throws FileNotFoundException,IOException {
        StringBuffer stringBuffer = new StringBuffer();
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) stringBuffer.append(st+"\n");
        return stringBuffer.toString();

    }

}
