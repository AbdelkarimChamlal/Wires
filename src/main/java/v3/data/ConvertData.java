package v3.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import v3.models.Configs;
import v3.models.CrimpingRow;
import v3.models.MaxRow;
import v3.models.Revision;
import v3.standards.Table;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
            Row row = rowIterator.next();
            v3.standards.Row row1 = new v3.standards.Row();
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
            table.getRows().add(row1);
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

    public static void fillSheetWithTable(Sheet sheet, v3.standards.Table table){
        int rowOrder = 0;
        for (v3.standards.Row row:table.getRows()){
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

    public static MaxRow convertRowIntoMaxRow(v3.standards.Row row, Configs maxConfigs, List<String> columns){
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

    public static CrimpingRow convertRowIntoDoubleRow(v3.standards.Row row, Configs crimpingConfigs, List<String> columns){
        CrimpingRow crimpingRow = new CrimpingRow();

        crimpingRow.setConnectorName(row.getValue(columns.indexOf(crimpingConfigs.getConfigValue("connectorName"))));
        crimpingRow.setCavity(row.getValue(columns.indexOf(crimpingConfigs.getConfigValue("cavity"))));
        crimpingRow.setWireCustomerName(row.getValue(columns.indexOf(crimpingConfigs.getConfigValue("wireCustomerName"))));
        crimpingRow.setCrimpingType(row.getValue(columns.indexOf(crimpingConfigs.getConfigValue("crimpingType"))));
        crimpingRow.setWireFM(row.getValue(columns.indexOf(crimpingConfigs.getConfigValue("wireFM"))));
        crimpingRow.setCrimpingDouble(row.getValue(columns.indexOf(crimpingConfigs.getConfigValue("crimpingDouble"))));

        crimpingRow.setColumns(columns);
        crimpingRow.setValues(row.getValues());

        return crimpingRow;
    }

    public static String convertToRevisionText(String hash, Revision revision){
        return "\""+hash+"\""+"::"+"\""+revision.getWirePM()+"\""+"::"+"\""+revision.getWireSK()+"\""+"::"+"\""+revision.getDoublePM()+"\""+"::"+"\""+revision.getDoubleSK()+"\""+"::"+"\""+revision.getTwistPM()+"\""+"::"+"\""+revision.getTwistSK()+"\"";
    }

    public static Map<String, Revision> convertStringToRevisions(String text){
        Map<String, Revision> revisionMap = new HashMap<>();
        String[] revisions = text.split("\n");
        for(int i = 0 ; i < revisions.length; i++){
            if(!revisions[i].startsWith("#") && revisions[i].length()>2){

                String[] values = revisions[i].split("::");
                for(int j = 0 ; j < values.length ; j++){
                    if(values[j].startsWith("\"") && values[j].endsWith("\"")){
                        values[j] = values[j].substring(1,values[j].length()-1);
                    }
                }

                Revision revision = new Revision();

                revision.setWirePM(values[1]);
                revision.setTwistSK(values[2]);
                revision.setDoublePM(values[3]);
                revision.setDoubleSK(values[4]);
                revision.setTwistPM(values[5]);
                revision.setWireSK(values[6]);

                revisionMap.put(values[0],revision);
            }
        }
        return revisionMap;
    }



}
