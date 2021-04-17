package v3.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import v3.models.Configs;
import v3.models.Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * handles the conversion of data from a certain format into another
 */
public class ConvertData {

    /**
     * converts a sheet into a java table
     * @param sheet the input data in a sheet format
     * @return a 2D array in table format
     */
    public static Table convertSheetIntoTable(Sheet sheet){
        Table table = new Table();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while(rowIterator.hasNext()){
            List<String> line = new ArrayList<>();
            Row row = rowIterator.next();
            for(int i =0;i<row.getLastCellNum() ; i++){
                try{
                    Cell cell = row.getCell(i);
                    if(cell.getCellType()== CellType.NUMERIC){
                        line.add(String.valueOf((long) cell.getNumericCellValue()));
                    }else{
                        line.add(cell.getStringCellValue());
                    }
                }catch(Exception e){
                    line.add("");
                }
            }
            table.getRows().add(line);
        }
        return table;
    }

    public static Configs convertStringToConfigs(String text){
        Configs configs = new Configs();
        String[] confs = text.split("\n");
        for(int i = 0 ; i < confs.length; i++){
            if(!confs[i].startsWith("#") && confs[i].length()>2){
                String[] values = confs[i].split("=");
                configs.getValues().put(values[0],values[1]);
            }
        }
        return configs;
    }
}
