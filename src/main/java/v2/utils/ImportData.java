package v2.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * import data from specific inputType into a java table "List<List<String>>"
 */
public class ImportData {
    /**
     * import a sheet from an excel file and turn it into a java table
     * @param filePath file path compared to the resource file
     * @param sheetNum Sheet order number inside the excel file starting from 0
     * @return java 2D table
     * @throws IOException in case the file that we are trying to read is not available
     */
    public static List<List<String>> importWorkSheet(String filePath, int sheetNum) throws IOException {
        File file = new File(filePath);
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheetAt(sheetNum);
        return Convertor.convertXlsxSheetUsingPOIToList(sheet);
    }

    /**
     * import a sheet from an excel file and turn it into a java table
     * @param filePath file path compared to the resource file
     * @param sheetName Sheet name inside the excel file
     * @return java 2D table
     * @throws IOException in case the file that we are trying to read is not available
     */
    public static List<List<String>> importWorkSheet(String filePath,String sheetName) throws IOException {
        File file = new File(filePath);
        Workbook workbook = WorkbookFactory.create(file);
        Sheet sheet = workbook.getSheet(sheetName);
        return Convertor.convertXlsxSheetUsingPOIToList(sheet);
    }

    public static List<String> getAllDirectories(String path){
        List<String> allDirectories = new ArrayList<>();
        final File folder = new File(path);
        for(final File fileEntry:folder.listFiles()){
            if (fileEntry.isDirectory()){
                allDirectories.add(fileEntry.getName());
            }
        }
        return allDirectories;
    }

}

