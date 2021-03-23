import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.DoubleGenerator;
import utils.HeaderGenerator;
import utils.OutputGenerator;
import utils.RowHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddDoubles {

    static String maxPath = "/max.xlsx";
    static String crimpingPath = "/crimping.xlsx";
    static String outputPath     = "C:\\Users\\Abdel\\OneDrive\\Desktop\\yazaki\\xlsx git\\src\\main\\resources\\new max.xlsx";

    static int maxUniqueKey = 5;
    static int crimpingUniqueKey = 5;



    public static void main(String[] args) {
        //load old version book
        InputStream maxStream = AddDoubles.class.getResourceAsStream(maxPath);
        Workbook maxBook = null;
        try {
            maxBook = new XSSFWorkbook(maxStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            maxStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load new version book
        InputStream crimpingStream = AddDoubles.class.getResourceAsStream(crimpingPath);
        Workbook crimpingBook = null;
        try {
            crimpingBook = new XSSFWorkbook(crimpingStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            crimpingStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load max sheet
        assert maxBook != null;
        Sheet maxSheet = maxBook.getSheetAt(0);

        //load crimping sheet
        assert crimpingBook != null;
        Sheet crimpingSheet = crimpingBook.getSheetAt(0);

        //maxHeader
        Row maxHeader = maxSheet.getRow(0);

        //crimpingHeader
        Row crimpingHeader = crimpingSheet.getRow(0);

        //get maxHeaderValues
        List<String> maxHeaderValues = HeaderGenerator.getHeaderValues(maxHeader);

        //get crimpingHeaderValues
        List<String> crimpingHeaderValues = HeaderGenerator.getHeaderValues(crimpingHeader);

        //get all max rows
        List<Row> maxRows = RowHandler.getAllRows(maxSheet);

        //get all crimping rows
        List<Row> crimpingRows = RowHandler.getAllRows(crimpingSheet);

        List<Row> addedRows = new ArrayList<>();

        //delete empty rows || column number < key position
        RowHandler.deleteEmptyRows(crimpingRows, crimpingUniqueKey);
        RowHandler.deleteEmptyRows(maxRows, maxUniqueKey);

        List<String> wiresInMax = RowHandler.getDifferentValues(maxRows,maxUniqueKey);
        List<String> wiresInCrimping = RowHandler.getDifferentValues(crimpingRows,crimpingUniqueKey);

        List<List<Row>> wiresListInMax = RowHandler.getRowsWithSameKey(maxRows,wiresInMax,maxUniqueKey);
        List<List<Row>> wiresListInCrimping = RowHandler.getRowsWithSameKey(crimpingRows,wiresInCrimping,crimpingUniqueKey);

        //get common keys between crimping and max
        List<String> commonKeys = DoubleGenerator.commonKeys(wiresInMax,wiresInCrimping);

        List<List<Row>> commonInMax = DoubleGenerator.deleteUnCommonKeysAndRows(commonKeys,wiresInMax,wiresListInMax);
        List<List<Row>> commonInCrimping = DoubleGenerator.deleteUnCommonKeysAndRows(commonKeys,wiresInCrimping,wiresListInCrimping);

        //get rows with double only wireType column is : 13
        List<List<Row>> rowsWithDoubleCrimping = DoubleGenerator.hasDouble(commonInCrimping,13);

        //get rows with double from Max "from Connector"
        List<List<Row>> rowsWithDoubleInMaxAtSource = DoubleGenerator.hasDouble(commonInMax,33);

        //get rows with double from Max "to Connector"
        List<List<Row>> rowsWithDoubleInMaxAtDestination = DoubleGenerator.hasDouble(commonInMax,52);

        //fromConnectorIndex : 19
        //ToConnectorIndex   : 38
        //VM code            : 11

        //first step check if we have a double in both crimping and max

        for(int i = 0 ; i< rowsWithDoubleCrimping.size();i++){
            if(rowsWithDoubleCrimping.get(i).size()>0){
                for(int j = 0; j< rowsWithDoubleCrimping.get(i).size() ; j++){
                    for(int h = 0 ; h < commonInMax.get(i).size() ; h++){

                        System.out.print(rowsWithDoubleCrimping.get(i).get(j).getCell(11).getStringCellValue().toLowerCase() +"::");
                        System.out.print(commonInMax.get(i).get(h).getCell(0).getStringCellValue().toLowerCase()+"\n");
                        if(rowsWithDoubleCrimping.get(i).get(j).getCell(11).getStringCellValue().toLowerCase().contains(commonInMax.get(i).get(h).getCell(0).getStringCellValue().toLowerCase())){
                            System.out.println("Got a case");
                        }

                    }
                }
            }
        }








        //now its time to start collecting all data together and form it into a new sheet with all changes

        //create new workBook
        Workbook outputBook = new XSSFWorkbook();

        //create the output sheet and name it tickets
        Sheet outputSheet = outputBook.createSheet("results");

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
