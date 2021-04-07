package v1;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import v1.utils.DoubleGenerator;
import v1.utils.HeaderGenerator;
import v1.utils.RowHandler;

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

    static int crimpingIndexInSheet = 0;
    static int maxIndexInSheet = 0;

    static int headerPositionInCrimping = 0;
    static int headerPositionInMax = 0;

    static int wireTypePositionInCrimping = 13;
    static int wireDoubleCrimpingPositionInCrimping = 14;

    static int wireTypePositionInMaxAtSource = 33;
    static int wireTypePositionInMaxAtDestination = 52;

    static int doubleCrimpingPositionInMaxAtSource = 34;
    static int doubleCrimpingPositionInMaxAtDestination = 53;

    static int crimpingModelsPosition = 11;
    static int maxModelPosition = 0;

    static int crimpingConnectorPosition = 2;
    static int maxConnectorPositionAtSource = 19;
    static int maxConnectorPositionAtDestination = 39;

    static int cavityPositionInCrimping = 1;
    static int cavityPositionInMaxAtSource = 25;
    static int cavityPositionInMaxAtDestination = 44;

    static int terminalPositionInCrimping = 15;
    static int terminalPositionInMaxAtSource = 26;
    static int terminalPositionInMaxAtDestination = 45;




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
        Sheet maxSheet = maxBook.getSheetAt(maxIndexInSheet);

        //load crimping sheet
        assert crimpingBook != null;
        Sheet crimpingSheet = crimpingBook.getSheetAt(crimpingIndexInSheet);

        //maxHeader
        Row maxHeader = maxSheet.getRow(headerPositionInMax);

        //crimpingHeader
        Row crimpingHeader = crimpingSheet.getRow(headerPositionInCrimping);

        //get maxHeaderValues
        List<String> maxHeaderValues = HeaderGenerator.getHeaderValues(maxHeader);

        //get crimpingHeaderValues
        List<String> crimpingHeaderValues = HeaderGenerator.getHeaderValues(crimpingHeader);

        //get all max rows
        List<Row> maxRows = RowHandler.getAllRows(maxSheet);

        //get all crimping rows
        List<Row> crimpingRows = RowHandler.getAllRows(crimpingSheet);

        List<Row> addedRows = new ArrayList<>();

        //delete empty rows || column number < header size
        RowHandler.deleteEmptyRows(crimpingRows, crimpingUniqueKey);
        RowHandler.deleteEmptyRows(maxRows, maxUniqueKey);

        List<String> wiresInMax = RowHandler.getDifferentValues(maxRows,maxUniqueKey);
        List<String> wiresInCrimping = RowHandler.getDifferentValues(crimpingRows,crimpingUniqueKey);

        //get common keys between crimping and max
        List<String> commonKeys = DoubleGenerator.commonKeys(wiresInMax,wiresInCrimping);

        Map<String,List<Row>> crimpingGroups = DoubleGenerator.groupGenerator(commonKeys,crimpingRows,crimpingUniqueKey);
        Map<String,List<Row>> maxGroups = DoubleGenerator.groupGenerator(commonKeys,maxRows,maxUniqueKey);

        int matchesFound = 0;
        for(String key:commonKeys){
            List<Row> crimpingGroup = crimpingGroups.get(key);
            for(Row crimpingRow:crimpingGroup){

                //if this boi got some double in it
                if(crimpingRow.getCell(wireTypePositionInCrimping)!=null)
                if(crimpingRow.getCell(wireTypePositionInCrimping).getStringCellValue().equalsIgnoreCase("double")){

                    //get them bois from the max list with same wire costumed ids
                    List<Row> maxGroup = maxGroups.get(key);
                    List<Row> matchingModels = new ArrayList<>();

                    String crimpingConnectors = crimpingRow.getCell(crimpingConnectorPosition).getStringCellValue();
                    String crimpingModels = crimpingRow.getCell(crimpingModelsPosition).getStringCellValue();
                    String crimpingCavity = crimpingRow.getCell(cavityPositionInCrimping).getStringCellValue();

                    //check if the max bois match the crimping boi in v1.models
                    //and add them to the matching bois list
                    for(Row maxRow:maxGroup){
                        if(crimpingModels.contains(maxRow.getCell(maxModelPosition).getStringCellValue())){
                            matchingModels.add(maxRow);
                        }
                    }

                    List<Row> matchingModelsAndConnectors = new ArrayList<>();

                    //now check if the matching model bois match with crimping boi on level of the connector too
                    //and add them to the matching v1.models and connectors
                    for(Row matchingModel:matchingModels){

                        String maxConnectorAtSource = matchingModel.getCell(maxConnectorPositionAtSource).getStringCellValue();
                        String maxConnectorAtDestination = matchingModel.getCell(maxConnectorPositionAtDestination).getStringCellValue();

                        if(crimpingConnectors.contains(maxConnectorAtDestination) || crimpingConnectors.contains(maxConnectorAtSource) || maxConnectorAtDestination.contains(crimpingConnectors) || maxConnectorAtSource.contains(crimpingConnectors)){
                            matchingModelsAndConnectors.add(matchingModel);
                        }
                    }

                    List<Row> matchingModelsAndConnectorsAndCavity = new ArrayList<>();

                    for(Row matching:matchingModelsAndConnectors){

                        String maxCavityAtSource = matching.getCell(cavityPositionInMaxAtSource).getStringCellValue();
                        String maxCavityAtDestination = matching.getCell(cavityPositionInMaxAtDestination).getStringCellValue();

                        if(crimpingCavity.contains(maxCavityAtDestination) || crimpingCavity.contains(maxCavityAtSource) || maxCavityAtSource.contains(crimpingCavity) || maxCavityAtDestination.contains(crimpingCavity)){
                            matchingModelsAndConnectorsAndCavity.add(matching);
                        }
                    }

                    boolean hasDouble = false;

                    //now then matching in both models and connectors bois are some very bad boys
                    //we need to see if they are doubled or not
                    //if they are double then fine
                    //if not the hasDouble flag will stay false
                    //and this bois ladies and gentlemen needs to be added to the added rows and put them back to the max list

                    for(Row matching : matchingModelsAndConnectorsAndCavity){

                        String wireTypeAtSource = matching.getCell(wireTypePositionInMaxAtSource).getStringCellValue();
                        String connectorAtSource = matching.getCell(maxConnectorPositionAtSource).getStringCellValue();

                        String wireTypeAtDestination = matching.getCell(wireTypePositionInMaxAtDestination).getStringCellValue();
                        String connectorAtDestination = matching.getCell(wireTypePositionInMaxAtDestination).getStringCellValue();

                        if(wireTypeAtSource.equalsIgnoreCase("double") && crimpingConnectors.contains(connectorAtSource)){
                            hasDouble=true;
                        }
                        if(wireTypeAtDestination.equalsIgnoreCase("double") && crimpingConnectors.contains(connectorAtDestination)){
                            hasDouble=true;
                        }
                    }

                    // now if the flag is still false
                    // this means we need to take actions
                    // first step of the action
                    // detect where the matching happened since we know there is a match
                    // we just need to detect if the match should be on the from side or the to side
                    // and when we know what side we should edit
                    // we create a copy of that row
                    // replay the crimping type from single or empty into double
                    // then lads we add the double crimp to the right side
                    // and kaboom we done here
                    // the case of having a single and should be turned in double is not me business right now
                    // you may ask why check the bois size ? the flag would stay false in case of no matching was there to begin with
                    // there for we need to check

                    if(!hasDouble && matchingModelsAndConnectorsAndCavity.size()>0){
                        //there should be only one matching but in case of duplicated values i dunno it may cause some problems?
                        for(Row takeActionRow:matchingModelsAndConnectors){

                            //new i should check if i should add or just change values
                            boolean add = false;

                            for(Row row:crimpingGroup){

                                boolean cavityAtSource = RowHandler.checkForSameValues(takeActionRow,row,cavityPositionInMaxAtSource,cavityPositionInCrimping);
                                boolean cavityAtDestination = RowHandler.checkForSameValues(takeActionRow,row,cavityPositionInMaxAtDestination,cavityPositionInCrimping);

                                boolean connectorAtSource = RowHandler.checkForSameValues(takeActionRow,row,maxConnectorPositionAtSource,crimpingConnectorPosition);
                                boolean connectorAtDestination = RowHandler.checkForSameValues(takeActionRow,row,maxConnectorPositionAtDestination,crimpingConnectorPosition);

                                boolean model = RowHandler.checkForSameValues(takeActionRow,row,maxModelPosition,crimpingModelsPosition);
                                boolean single = row.getCell(wireTypePositionInCrimping).getStringCellValue().toLowerCase().contains("single");

                                if(cavityAtSource && connectorAtSource && model && single){
                                    add = true;
                                }

                                if(cavityAtDestination && connectorAtDestination && model && single){
                                    add = true;
                                }
                            }


                            String wireTypeAtSource = takeActionRow.getCell(wireTypePositionInMaxAtSource).getStringCellValue();
                            String connectorAtSource = takeActionRow.getCell(maxConnectorPositionAtSource).getStringCellValue();
                            String wireTypeAtDestination = takeActionRow.getCell(wireTypePositionInMaxAtDestination).getStringCellValue();
                            String connectorAtDestination = takeActionRow.getCell(maxConnectorPositionAtDestination).getStringCellValue();

                            boolean notDoubleAtSource = !wireTypeAtSource.equalsIgnoreCase("double") && crimpingConnectors.contains(connectorAtSource);
                            boolean notDoubleAtDestination = !wireTypeAtDestination.equalsIgnoreCase("double") && crimpingConnectors.contains(connectorAtDestination);

                            if(add){

                                Row newRow = crimpingSheet.createRow(maxRows.size() + matchesFound + 1);
                                matchesFound++;

                                for(int i = 0; i<takeActionRow.getLastCellNum() ; i++){
                                    Cell newCell = newRow.createCell(i);
                                    if(takeActionRow.getCell(i)!=null){
                                        newCell.setCellValue(takeActionRow.getCell(i).getStringCellValue());
                                    }
                                }

                                if(notDoubleAtSource){
                                    newRow.getCell(wireTypePositionInMaxAtSource).setCellValue("Double");
                                    newRow.getCell(terminalPositionInMaxAtSource).setCellValue(crimpingRow.getCell(terminalPositionInCrimping).getStringCellValue());
                                    String doubleWith = crimpingRow.getCell(wireDoubleCrimpingPositionInCrimping).getStringCellValue();
                                    if(doubleWith.contains(key+",")){
                                        doubleWith = doubleWith.replace(key+",","");
                                    }
                                    if(doubleWith.contains(","+key)){
                                        doubleWith = doubleWith.replace(","+key,"");
                                    }
                                    newRow.getCell(doubleCrimpingPositionInMaxAtSource).setCellValue(doubleWith);
                                }

                                if(notDoubleAtDestination){
                                    newRow.getCell(wireTypePositionInMaxAtDestination).setCellValue("Double");
                                    newRow.getCell(terminalPositionInMaxAtDestination).setCellValue(crimpingRow.getCell(terminalPositionInCrimping).getStringCellValue());
                                    String doubleWith = crimpingRow.getCell(wireDoubleCrimpingPositionInCrimping).getStringCellValue();
                                    if(doubleWith.contains(key+",")){
                                        doubleWith = doubleWith.replace(key+",","");
                                    }
                                    if(doubleWith.contains(","+key)){
                                        doubleWith = doubleWith.replace(","+key,"");
                                    }
                                    newRow.getCell(doubleCrimpingPositionInMaxAtDestination).setCellValue(doubleWith);
                                }
                                addedRows.add(newRow);

                            }else{

                                if(notDoubleAtSource){
                                    takeActionRow.getCell(wireTypePositionInMaxAtSource).setCellValue("Double");
                                    takeActionRow.getCell(terminalPositionInMaxAtSource).setCellValue(crimpingRow.getCell(terminalPositionInCrimping).getStringCellValue());
                                    String doubleWith = crimpingRow.getCell(wireDoubleCrimpingPositionInCrimping).getStringCellValue();
                                    if(doubleWith.contains(key+",")){
                                        doubleWith = doubleWith.replace(key+",","");
                                    }
                                    if(doubleWith.contains(","+key)){
                                        doubleWith = doubleWith.replace(","+key,"");
                                    }
                                    takeActionRow.getCell(doubleCrimpingPositionInMaxAtSource).setCellValue(doubleWith);
                                }

                                if(notDoubleAtDestination){
                                    takeActionRow.getCell(wireTypePositionInMaxAtDestination).setCellValue("Double");
                                    takeActionRow.getCell(terminalPositionInMaxAtDestination).setCellValue(crimpingRow.getCell(terminalPositionInCrimping).getStringCellValue());
                                    String doubleWith = crimpingRow.getCell(wireDoubleCrimpingPositionInCrimping).getStringCellValue();
                                    if(doubleWith.contains(key+",")){
                                        doubleWith = doubleWith.replace(key+",","");
                                    }
                                    if(doubleWith.contains(","+key)){
                                        doubleWith = doubleWith.replace(","+key,"");
                                    }
                                    takeActionRow.getCell(doubleCrimpingPositionInMaxAtDestination).setCellValue(doubleWith);
                                }
                            }


                        }
                    }
                }
            }
        }











        //now its time to start collecting all data together and form it into a new sheet with all changes

        //create new workBook
        Workbook outputBook = new XSSFWorkbook();

        Sheet outputSheet = outputBook.createSheet("results");

        //header and shit
        Row outputHeader = outputSheet.createRow(0);
        for(int i = 0 ; i<maxHeaderValues.size() ; i++){
            Cell cell = outputHeader.createCell(i);
            if(maxHeaderValues.get(i)!=null){
                cell.setCellValue(maxHeaderValues.get(i));
            }
        }

        //put values in
        for(int i = 1 ; i<=maxRows.size() ; i++){
            Row row = outputSheet.createRow(i);
            for(int j = 0;j<maxRows.get(i-1).getLastCellNum();j++){
                Cell cell = row.createCell(j);
                if(maxRows.get(i-1).getCell(j)!=null){
                    cell.setCellValue(maxRows.get(i-1).getCell(j).getStringCellValue());
                }
            }
        }

        //add the values
        for(int i =0; i<addedRows.size(); i++){
            Row row = outputSheet.createRow(i+maxRows.size()+1);
            for(int j = 0;j<addedRows.get(i).getLastCellNum();j++){
                Cell cell = row.createCell(j);
                if(addedRows.get(i).getCell(j)!=null){
                    cell.setCellValue(addedRows.get(i).getCell(j).getStringCellValue());
                }
            }
        }

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
