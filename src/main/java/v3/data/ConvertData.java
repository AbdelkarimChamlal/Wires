package v3.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import v3.models.Configs;
import v3.interfaces.Table;
import v3.models.MaxRow;

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
        Table table = new v3.primitiveModels.Table();
        Iterator<Row> rowIterator = sheet.rowIterator();
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            v3.interfaces.Row row1 = new v3.primitiveModels.Row();
            for(int i =0;i<row.getLastCellNum() ; i++){
                try{
                    Cell cell = row.getCell(i);
                    if(cell.getCellType()== CellType.NUMERIC){
                        row1.addValue(String.valueOf((long) cell.getNumericCellValue()));
                    }else{
                        row1.addValue(cell.getStringCellValue());
                    }
                }catch(Exception e){
                    row1.addValue("");
                }
            }
            table.getRows().add((v3.primitiveModels.Row) row1);
        }
        return table;
    }

    public static Configs convertStringToConfigs(String text){
        Configs configs = new Configs();
        String[] confs = text.split("\n");
        for(int i = 0 ; i < confs.length; i++){
            if(!confs[i].startsWith("#") && confs[i].length()>2){
                String[] values = confs[i].split("=");
                String key = values[0];
                String value = values[1];

                if(value.startsWith("\"") && value.endsWith("\"")){
                    value = value.substring(1,value.length()-1);
                }
                configs.getValues().put(key,value);
            }
        }
        return configs;
    }

    public static void fillSheetWithTable(Sheet sheet, v3.primitiveModels.Table table){
        int rowOrder = 0;
        for (v3.primitiveModels.Row row:table.getRows()){
            Row rowInSheet = sheet.createRow(rowOrder);
            rowOrder++;
            int cellOrder = 0;
            for(String cellValue:row.getValues()){
                Cell cell = rowInSheet.createCell(cellOrder);
                cellOrder++;
                cell.setCellValue(cellValue);
            }
        }
    }

    public static MaxRow convertRowIntoMaxRow(v3.primitiveModels.Row row, Configs maxConfigs, List<String> columns){
        MaxRow maxRow = new MaxRow();

        maxRow.setModuleName(row.getValue(columns.indexOf(maxConfigs.getConfigValue("moduleName"))));
        maxRow.setModulePIN(row.getValue(columns.indexOf(maxConfigs.getConfigValue("modulePIN"))));
        maxRow.setWireKey(row.getValue(columns.indexOf(maxConfigs.getConfigValue("wireKey"))));
        maxRow.setFromSource(row.getValue(columns.indexOf(maxConfigs.getConfigValue("fromSource"))));
        maxRow.setFromCavity(row.getValue(columns.indexOf(maxConfigs.getConfigValue("fromCavity"))));
        maxRow.setFromCrimpingType(row.getValue(columns.indexOf(maxConfigs.getConfigValue("fromCrimpingType"))));
        maxRow.setFromCrimpingDouble(row.getValue(columns.indexOf(maxConfigs.getConfigValue("fromCrimpingDouble"))));
        maxRow.setToSource(row.getValue(columns.indexOf(maxConfigs.getConfigValue("toSource"))));
        maxRow.setToCavity(row.getValue(columns.indexOf(maxConfigs.getConfigValue("toCavity"))));
        maxRow.setToCrimpingType(row.getValue(columns.indexOf(maxConfigs.getConfigValue("toCrimpingType"))));
        maxRow.setToCrimpingDouble(row.getValue(columns.indexOf(maxConfigs.getConfigValue("toCrimpingDouble"))));

        maxRow.setColumns(columns);
        maxRow.setValues(row.getValues());

        return maxRow;
    }
}
