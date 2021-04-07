package v2.services;

import org.apache.commons.collections4.map.HashedMap;
import v2.helpers.Values;
import v2.utils.ExportData;
import v2.utils.ImportData;
import v2.utils.RowUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static v2.helpers.Values.*;

/**
 * detect if a wire can be called by two different models
 */
public class CompatibilityDetector {
    List<List<String>> maxTable;
    List<List<String>> plausibilityTable;
    List<List<String>> crimpingTable;
    List<List<String>> finalCrimpingFile;
    List<List<String>> expandCrimping;
    List<List<String>> errors;
    List<String> errorRows;
    int idPosition;
    int idPositionCrimping;

    /**
     * initialize an instant of the comparing class and fulfill
     * the requirement for the comparing process to start.
     *
     * @param maxPath the path to the max excel file in resource files
     * @param plausibilityPath the path to the plausibility file in resource files
     * @param crimpingPath the path to the crimping file in the resource files
     * @throws IOException in case of a messing or non unable to read file
     */

    public CompatibilityDetector(String maxPath, String plausibilityPath,String crimpingPath) throws IOException {
        this.maxTable = ImportData.importWorkSheet(maxPath, Values.MAX_ORDER_IN_SHEET);
        this.crimpingTable = ImportData.importWorkSheet(crimpingPath, Values.CRIMPING_ORDER_IN_SHEET);
        this.plausibilityTable = ImportData.importWorkSheet(plausibilityPath,Values.PLAUSIBILITY_NAME_IN_SHEET);
    }

    /**
     * start the comparing process between and crimping table and the plausibility table
     * max table is used to map from FM ping values and FM code values.
     */
    public void startComparing() throws IOException {

        //format plausibility table and generate FMP and FMC map
        formatPlausibilityTable();

        finalCrimpingFile = new ArrayList<>();

        finalCrimpingFile.add(RowUtil.duplicateRow(crimpingTable.get(0)));

        // TODO:explain this
        idPosition = maxTable.get(0).size();
        idPositionCrimping = crimpingTable.get(0).size();
        RowUtil.addId(maxTable,idPosition);
        RowUtil.addId(crimpingTable,idPositionCrimping);


        List<String> uniqueConnectorsToDirection = RowUtil.uniqueValues(maxTable,MAX_TO_INTERNAL);
        List<String> uniqueConnectorsFromDirection = RowUtil.uniqueValues(maxTable,MAX_FROM_INTERNAL);



        // remove header value
        uniqueConnectorsToDirection.remove(0);
        uniqueConnectorsFromDirection.remove(0);

        for(int i = 0 ; i<uniqueConnectorsToDirection.size();i++){
            if(uniqueConnectorsToDirection.get(i).startsWith("J") || uniqueConnectorsToDirection.get(i).contains("-")){
                uniqueConnectorsToDirection.remove(i);
                i--;
            }
        }

        for(int i = 0 ; i<uniqueConnectorsFromDirection.size();i++){
            if(uniqueConnectorsFromDirection.get(i).startsWith("J") || uniqueConnectorsFromDirection.get(i).contains("-")){
                uniqueConnectorsFromDirection.remove(i);
                i--;
            }
        }

        List<String> uniqueConnectors = uniqueConnectorsToDirection;
        for(String uniqueFrom:uniqueConnectorsFromDirection){
            if(!uniqueConnectorsToDirection.contains(uniqueFrom)){
                uniqueConnectors.add(uniqueFrom);
            }
        }



        // prepare the fmc_fmp map and the plausibility map
        Map<String,String> FMC_FMP = RowUtil.generateMap(RowUtil.getColumn(maxTable,FMC_ORDER_IN_MAX),RowUtil.getColumn(maxTable,FMP_ORDER_IN_MAX));
        Map<String,Map<String,List<String>>> matchingMap = matchingMap(plausibilityTable);

        Map<String,List<String>> connectorCavities = new HashedMap<>();

        // to direction
        for(String connector:uniqueConnectors){
            List<List<String>> rowsWithSameConnector = RowUtil.getRowsByColumnValue(maxTable,connector,MAX_TO_INTERNAL);
            List<String> cavities = RowUtil.getColumn(rowsWithSameConnector,MAX_TO_CAVITY);

            for(int i = 0 ; i < cavities.size(); i++){
                for(int j = i+1;j<cavities.size();j++){
                    if (cavities.get(i).equals(cavities.get(j))){
                        cavities.remove(j);
                        j--;
                    }
                }
            }

            connectorCavities.put(connector,cavities);
        }

        for(String connector:uniqueConnectors){
            List<List<String>> rowsWithSameConnector = RowUtil.getRowsByColumnValue(maxTable,connector,MAX_FROM_INTERNAL);
            List<String> cavities = RowUtil.getColumn(rowsWithSameConnector,MAX_FROM_CAVITY);
            for(int i = 0 ; i < cavities.size(); i++){
                for(int j = i+1;j<cavities.size();j++){
                    if (cavities.get(i).equals(cavities.get(j))){
                        cavities.remove(j);
                        j--;
                    }
                }
            }

            if(connectorCavities.containsKey(connector)){
                for(String cavity:cavities){
                    connectorCavities.get(connector).add(cavity);
                }
            }else{
                connectorCavities.put(connector,cavities);
            }
        }

        Map<String,Map<String,List<String>>> connectorCavityRows = new HashedMap<>();

        for(String connector:uniqueConnectors){
            Map<String,List<String>> cavityRows = new HashedMap<>();
            for(String cavity:connectorCavities.get(connector)){
                List<String> rows = new ArrayList<>();
                for(List<String> row:maxTable){
                    if((row.get(MAX_TO_INTERNAL).equals(connector) && row.get(MAX_TO_CAVITY).equals(cavity)) || (row.get(MAX_FROM_INTERNAL).equals(connector) && row.get(MAX_FROM_CAVITY).equals(cavity))){
                        rows.add(row.get(idPosition));
                    }
                }
                cavityRows.put(cavity,rows);
            }
            connectorCavityRows.put(connector,cavityRows);
        }


        for(String connector:uniqueConnectors){
            for(String cavity:connectorCavities.get(connector)){
                if(connectorCavityRows.get(connector).get(cavity).size()==1){

                    // single row
                    List<String> singleRow = RowUtil.getRowByUniqueId(maxTable,connectorCavityRows.get(connector).get(cavity).get(0),idPosition);
                    finalCrimpingFile.add(singleRow(singleRow.get(idPosition),connector,cavity));

                }else{
                    for(String wireId1:connectorCavityRows.get(connector).get(cavity)){
                        for(String wireId2:connectorCavityRows.get(connector).get(cavity)){

                            List<String> row1 = RowUtil.getRowByUniqueId(maxTable,wireId1,idPosition);
                            List<String> row2 = RowUtil.getRowByUniqueId(maxTable,wireId2,idPosition);

                            if(!RowUtil.isDuplicated(row1,row2)){

                                if(row1.get(FMC_ORDER_IN_MAX).equalsIgnoreCase(row2.get(FMC_ORDER_IN_MAX))){
                                    finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
                                }else

                                if(matchingMap.get(row1.get(FMP_ORDER_IN_MAX)).get("n").contains(row2.get(FMP_ORDER_IN_MAX))){
                                    //this is the problem source
                                    //i need to find a solution for this
                                    //but for now it works fine if i ignore the added ones
                                    finalCrimpingFile.add(singleRow(row1.get(idPosition),connector,cavity));
                                    finalCrimpingFile.add(singleRow(row2.get(idPosition),connector,cavity));

                                }else

                                if(matchingMap.get(row1.get(FMP_ORDER_IN_MAX)).get("m").contains(row2.get(FMP_ORDER_IN_MAX))){
                                    finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
                                }else{
                                    finalCrimpingFile.add(singleRow(row1.get(idPosition),connector,cavity));
                                    finalCrimpingFile.add(singleRow(row2.get(idPosition),connector,cavity));

                                    finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
                                }
                            }
                        }
                    }
                }
            }
        }

//        for(String connector:uniqueConnectors){
//            for(String cavity:connectorCavities.get(connector)){
//                if(connectorCavityRows.get(connector).get(cavity).size()==1){
//
//                    //cavity has only one wire in it so it must be single
//                    List<String> maxRow = RowUtil.getRowByUniqueId(maxTable,connectorCavityRows.get(connector).get(cavity).get(0),idPosition);
//                    finalCrimpingFile.add(singleRow(maxRow.get(idPosition),connector,cavity));
//
//                }else{
//
//                    for(String wireId1:connectorCavityRows.get(connector).get(cavity)){
//
//                        List<String> row1 = RowUtil.getRowByUniqueId(maxTable,wireId1,idPosition);
//
//                        for(String wireId2:connectorCavityRows.get(connector).get(cavity)){
//
//                            List<String> row2 = RowUtil.getRowByUniqueId(maxTable,wireId2,idPosition);
//
//                            // wireId1 and wireId2 are different rows
//                            if(!row1.equals(row2)){
//
//                                //check in plausibility if the relationship between model1 and model2 doesn't allow for matching
//                                if(matchingMap.get(row1.get(FMP_ORDER_IN_MAX)).get("n").contains(row2.get(FMP_ORDER_IN_MAX))){
//
//                                    // in case the same module always double
//                                    if(row1.get(FMC_ORDER_IN_MAX).contains(row2.get(FMC_ORDER_IN_MAX)) || row2.get(FMC_ORDER_IN_MAX).contains(row1.get(FMC_ORDER_IN_MAX))){
//
//                                        finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
//
//                                    }else{
//
//                                        // if not then they should be singles
//                                        finalCrimpingFile.add(singleRow(row1.get(idPosition),connector,cavity));
//
//                                    }
//
//                                }else if(matchingMap.get(row1.get(FMP_ORDER_IN_MAX)).get("m").contains(row2.get(FMP_ORDER_IN_MAX))){
//
//                                    // if the relation ship between them is a must type then only double allowed
//                                    finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
//
//                                }else{
//                                    // can be single and double
//
//                                    if(row1.get(FMC_ORDER_IN_MAX).contains(row2.get(FMC_ORDER_IN_MAX)) || row2.get(FMC_ORDER_IN_MAX).contains(row1.get(FMC_ORDER_IN_MAX))){
//
//                                        // check if this thing has the same models again
//                                        // TODO: this should be updated i guess
//                                        finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
//
//                                    }else{
//
//                                        // single possibility
//                                        finalCrimpingFile.add(singleRow(row1.get(idPosition),connector,cavity));
//                                        // double possibility
//                                        finalCrimpingFile.add(doubleRow(row1,row2,connector,cavity));
//
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }

        for(int i = 1; i<finalCrimpingFile.size() ; i++){
            List<String> row1 = finalCrimpingFile.get(i);
            for(int j=i+1;j<finalCrimpingFile.size();j++){
                List<String> row2 = finalCrimpingFile.get(j);
                if(RowUtil.isDuplicated(row1,row2)){
                    finalCrimpingFile.remove(j);
                    j--;
                }
            }
        }

        ExportData.exportTableToExcel(RESOURCES_FOLDER+"max crimping possibilities.xlsx","crimping max",finalCrimpingFile);
    }

    public void compareCrimping(){
        errorRows = new ArrayList<>();
        expandCrimping = expandCrimpingTable();
        errors = new ArrayList<>();
        errors.add(RowUtil.duplicateRow(finalCrimpingFile.get(0)));
        errors.get(0).add("comments");
        // looking for extra wires in original crimping file
        for(int i = 1 ; i < expandCrimping.size() ; i++){
            List<String> crimpingRow = expandCrimping.get(i);
            boolean doubleFound = false;
            for(int j = 1 ; j < finalCrimpingFile.size() ; j++){
                List<String> finalRow = finalCrimpingFile.get(j);
                if(compareCrimping(crimpingRow,finalRow)){
                    doubleFound=true;
                }
            }
            if(!doubleFound){
                errorRows.add(crimpingRow.get(idPositionCrimping));
                List<String> errorRow = RowUtil.duplicateRow(crimpingRow);
                errorRow.add("should not exist");
                errors.add(errorRow);
            }
        }

        //looking for missing wires in original crimping report
        for(int i = 1 ; i < finalCrimpingFile.size() ; i++){
            List<String> finalRow = finalCrimpingFile.get(i);
            boolean doubleFound = false;
            for(int j = 1 ; j < expandCrimping.size() ; j++){
                List<String> crimpingRow = expandCrimping.get(j);
                if(compareCrimping(crimpingRow,finalRow)){
                    doubleFound=true;
                }
            }
            if(!doubleFound){
                List<String> errorRow = RowUtil.duplicateRow(finalRow);
                if(finalRow.get(CRIMPING_TYPE_IN_CRIMPING_REPORT).equalsIgnoreCase("double")) {
                    errorRow.add("missing from the crimping report");
                    errors.add(errorRow);
                    errorRows.add(finalRow.get(idPositionCrimping));

                }else{
                    finalCrimpingFile.remove(i);
                    i--;
                }
            }
        }

        for(int i = 1 ; i < crimpingTable.size();i++){
            if(!errorRows.contains(crimpingTable.get(i).get(idPositionCrimping))){
                List<String> validRow = RowUtil.duplicateRow(crimpingTable.get(i));
                validRow.add("valid row");
                errors.add(validRow);
            }
        }
    }

    public void exportErrors(String fileName) throws IOException {
        ExportData.exportTableToExcel(RESOURCES_FOLDER+fileName,"Errors",errors);
    }

    boolean compareCrimping(List<String> row1,List<String> row2){
        if(!row1.get(FMC_ORDER_IN_CRIMPING).equals(row2.get(FMC_ORDER_IN_CRIMPING)))return false;
        if(!row1.get(CRIMPING_WIRE_COSTUMER_POSITION).equals(row2.get(CRIMPING_WIRE_COSTUMER_POSITION)))return false;
        if(!row1.get(COSTUMER_CONNECTOR_IN_CRIMPING).equals(row2.get(COSTUMER_CONNECTOR_IN_CRIMPING)))return false;
        if(!row1.get(CAVITY_ORDER_IN_CRIMPING).equals(row2.get(CAVITY_ORDER_IN_CRIMPING)))return false;
        if(!row1.get(CRIMPING_TYPE_IN_CRIMPING_REPORT).equals(row2.get(CRIMPING_TYPE_IN_CRIMPING_REPORT)))return false;
        if(!row1.get(CRIMPING_INTERNAL).equals(row2.get(CRIMPING_INTERNAL)))return false;
        if(!row1.get(CRIMPING_DOUBLE).equals(row2.get(CRIMPING_DOUBLE)))return false;
        if(!row1.get(CRIMPING_WIRE_INTERNAL_NAME).equals(row2.get(CRIMPING_WIRE_INTERNAL_NAME)))return false;

        return true;
    }

    List<String> doubleRow(List<String> row1, List<String> row2,String connector , String cavity){
        List<String> row = RowUtil.emptyRow(finalCrimpingFile.get(0).size());

//        String fmc = (row1.get(FMC_ORDER_IN_MAX).equals(row2.get(FMC_ORDER_IN_MAX)))? row1.get(FMC_ORDER_IN_MAX) : row1.get(FMC_ORDER_IN_MAX) + " / "+ row2.get(FMC_ORDER_IN_MAX);

        row.set(FMC_ORDER_IN_CRIMPING,row1.get(FMC_ORDER_IN_MAX));
        row.set(CRIMPING_TYPE_IN_CRIMPING_REPORT,"Double");
        row.set(CRIMPING_WIRE_COSTUMER_POSITION,row1.get(MAX_WIRE_COSTUMER));
        row.set(CRIMPING_INTERNAL,connector);
        String crimpingDouble = (stringCompare(row1.get(MAX_WIRE_COSTUMER),row2.get(MAX_WIRE_COSTUMER))<0)?row1.get(MAX_WIRE_COSTUMER) +","+row2.get(MAX_WIRE_COSTUMER) : row2.get(MAX_WIRE_COSTUMER) +","+row1.get(MAX_WIRE_COSTUMER);

        row.set(CRIMPING_DOUBLE, crimpingDouble);

        row.set(CAVITY_ORDER_IN_CRIMPING,cavity);
        row.set(CRIMPING_WIRE_INTERNAL_NAME,row1.get(MAX_WIRE_INTERNAL_NAME));
        String costumerConnectorName = connector.equals(row1.get(MAX_FROM_INTERNAL))?row1.get(MAX_FROM_CONNECTOR):row1.get(MAX_TO_CONNECTOR);
        row.set(COSTUMER_CONNECTOR_IN_CRIMPING,costumerConnectorName);


        return row;
    }

    List<String> singleRow(String rowId,String connector,String cavity){

        List<String> row = RowUtil.emptyRow(finalCrimpingFile.get(0).size());
        List<String> rowMax = RowUtil.getRowByUniqueId(maxTable,rowId,idPosition);
        row.set(FMC_ORDER_IN_CRIMPING,rowMax.get(FMC_ORDER_IN_MAX));
        row.set(CRIMPING_TYPE_IN_CRIMPING_REPORT,"Single");
        row.set(CRIMPING_WIRE_COSTUMER_POSITION,rowMax.get(MAX_WIRE_COSTUMER));
        row.set(CRIMPING_DOUBLE,"-");
        row.set(CRIMPING_INTERNAL,connector);
        row.set(CAVITY_ORDER_IN_CRIMPING,cavity);
        row.set(CRIMPING_WIRE_INTERNAL_NAME,rowMax.get(MAX_WIRE_INTERNAL_NAME));
        String costumerConnectorName = connector.equals(rowMax.get(MAX_FROM_INTERNAL))?rowMax.get(MAX_FROM_CONNECTOR):rowMax.get(MAX_TO_CONNECTOR);
        row.set(COSTUMER_CONNECTOR_IN_CRIMPING,costumerConnectorName);

        return row;
    }

    private Map<String,Map<String,List<String>>> connectorCavityWires(List<String> connectorsInCrimping,List<List<String>> expandedCrimpingTable,Map<String,List<String>> cavitiesForConnector){
        Map<String,Map<String,List<String>>> CONNECTOR_CAVITIES_WIRES = new HashedMap<>();


        for(String connector:connectorsInCrimping){
            Map<String,List<String>> CAVITY_WIRES = new HashedMap<>();
            for(String cavity:cavitiesForConnector.get(connector)){
                List<String> WIRES = new ArrayList<>();
                for(List<String> row:expandedCrimpingTable){
                    if(row.get(COSTUMER_CONNECTOR_IN_CRIMPING).equalsIgnoreCase(connector) && row.get(CAVITY_ORDER_IN_CRIMPING).equalsIgnoreCase(cavity)){
                        WIRES.add(row.get(CRIMPING_WIRE_COSTUMER_POSITION));
                    }
                }
                CAVITY_WIRES.put(cavity,WIRES);
            }
            CONNECTOR_CAVITIES_WIRES.put(connector,CAVITY_WIRES);
        }
        return CONNECTOR_CAVITIES_WIRES;
    }


    private Map<String,List<String>> cavitiesForEachConnector(List<String> connectorsInCrimping,List<List<String>> expandedCrimpingTable){
        Map<String,List<String>> cavitiesForConnector = new HashedMap<>();


        for(String connector:connectorsInCrimping){
            cavitiesForConnector.put(connector,new ArrayList<>());
            for(List<String> crimpingRow:expandedCrimpingTable ){
                if(crimpingRow.get(COSTUMER_CONNECTOR_IN_CRIMPING).equalsIgnoreCase(connector)){
                    if(!cavitiesForConnector.get(connector).contains(crimpingRow.get(CAVITY_ORDER_IN_CRIMPING))){
                        cavitiesForConnector.get(connector).add(crimpingRow.get(CAVITY_ORDER_IN_CRIMPING));
                    }
                }
            }
        }

        return cavitiesForConnector;
    }


    private Map<String,List<String>> modelGroupByWires(List<String> wireKeys,List<List<String>> expandedCrimpingTable){
        Map<String,List<String>> modelsForEachWire = new HashedMap<>();
        for(String wireKey:wireKeys){
            List<String> wireModels = new ArrayList<>();
            for(List<String> row:expandedCrimpingTable){
                if(row.get(CRIMPING_WIRE_COSTUMER_POSITION).equalsIgnoreCase(wireKey) && !wireModels.contains(row.get(FMC_ORDER_IN_CRIMPING))){
                    wireModels.add(row.get(FMC_ORDER_IN_CRIMPING));
                }
            }
            modelsForEachWire.put(wireKey,wireModels);
        }
        return modelsForEachWire;
    }


    /**
     * format the private table Plausibility to make it easy to use.
     */
    private void formatPlausibilityTable(){

        // remove unwanted columns starting from the last one since the column
        // positions update when deleting a column
        RowUtil.removeColumn(plausibilityTable,10);
        RowUtil.removeColumn(plausibilityTable,9);
        RowUtil.removeColumn(plausibilityTable,8);
        RowUtil.removeColumn(plausibilityTable,7);
        RowUtil.removeColumn(plausibilityTable,6);
        RowUtil.removeColumn(plausibilityTable,5);
        RowUtil.removeColumn(plausibilityTable,4);
        RowUtil.removeColumn(plausibilityTable,2);
        RowUtil.removeColumn(plausibilityTable,1);

        // remove unwanted rows starting from the last one since the rows
        // positions update when deleting a row
        RowUtil.removeRow(plausibilityTable,2);
        RowUtil.removeRow(plausibilityTable,0);

        // update the header values
        plausibilityTable.get(0).set(0,"FMP");
        plausibilityTable.get(0).set(1,"FMC");

        // voila plausibilityTable is updated
    }

    private List<List<String>> expandCrimpingTable(){
        List<List<String>> expandedCrimpingTable = new ArrayList<>();

        for (List<String> crimpingRow:crimpingTable){
            if(crimpingRow.get(FMC_ORDER_IN_CRIMPING).contains("/")){
                String[] fmcS = crimpingRow.get(FMC_ORDER_IN_CRIMPING).split(" / ");
                for(String fmc:fmcS){
                    List<String> newRow = RowUtil.duplicateRow(crimpingRow);
                    newRow.set(FMC_ORDER_IN_CRIMPING,fmc);
                    expandedCrimpingTable.add(newRow);
                }
            }else{
                expandedCrimpingTable.add(crimpingRow);
            }
        }

        return expandedCrimpingTable;
    }

    private Map<String,Map<String,List<String>>> matchingMap(List<List<String>> plausibilityTable){
        Map<String,Map<String,List<String>>> plausibilityMap = new HashedMap<>();

        for(int i = 1; i<plausibilityTable.size(); i++){

            String fmp = plausibilityTable.get(i).get(0);

            List<String> matching = new ArrayList<>();
            List<String> nonMatching = new ArrayList<>();
            List<String> mustMatch = new ArrayList<>();
            List<String> optional = new ArrayList<>();

            //TODO: should understand it instead of ignoring it
            List<String> ignoreIt = new ArrayList<>();

            Map<String,List<String>> plausibilitySubMap = new HashedMap<>();

            plausibilitySubMap.put("v",matching);
            plausibilitySubMap.put("n",nonMatching);
            plausibilitySubMap.put("o",optional);
            plausibilitySubMap.put("m",mustMatch);
            plausibilitySubMap.put("w",ignoreIt);

            for(int j=2;j<plausibilityTable.get(0).size();j++){

                if(plausibilityTable.get(i).get(j).equalsIgnoreCase("v") && !plausibilitySubMap.get("v").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("v").add(plausibilityTable.get(0).get(j));
                }else if(plausibilityTable.get(i).get(j).equalsIgnoreCase("n") && !plausibilitySubMap.get("n").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("n").add(plausibilityTable.get(0).get(j));
                }else if(plausibilityTable.get(i).get(j).equalsIgnoreCase("m") && !plausibilitySubMap.get("m").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("m").add(plausibilityTable.get(0).get(j));
                }else if(plausibilityTable.get(i).get(j).equalsIgnoreCase("o") && !plausibilitySubMap.get("o").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("o").add(plausibilityTable.get(0).get(j));
                }else if(plausibilityTable.get(i).get(j).equalsIgnoreCase("w") && !plausibilitySubMap.get("w").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("w").add(plausibilityTable.get(0).get(j));
                }else{
                    plausibilitySubMap.get("o").add(plausibilityTable.get(0).get(j));
                }

            }

            plausibilityMap.put(fmp,plausibilitySubMap);
        }

        return plausibilityMap;
    }

    public static int stringCompare(String str1, String str2)
    {

        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);

        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);

            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }


        if (l1 != l2) {
            return l1 - l2;
        }

        else {
            return 0;
        }
    }

}
