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
    List<List<String>> errors;

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
        RowUtil.addId(maxTable,maxTable.get(0).size());
        List<String> uniqueConnectors = RowUtil.uniqueValues(maxTable,MAX_TO_CONNECTOR);

        // remove header value
        uniqueConnectors.remove(0);

        // TODO: ask med if i should remove the ones start with J
        for(int i = 0 ; i < uniqueConnectors.size() ; i++){
            List<List<String>> row = RowUtil.getRowsByColumnValue(maxTable,uniqueConnectors.get(i),MAX_TO_CONNECTOR);
            if(row.size()==0){
                System.out.println("something wrong here");
                System.out.println(uniqueConnectors.get(i));
            }else{
                if(row.get(0).get(MAX_TO_INTERNAL).contains("J")){
                    uniqueConnectors.remove(i);
                    i--;
                }
            }
        }


        // I had to stop music for this part
        // so you know this is some complex bullshit

        // TODO:explain this part too

        Map<String,List<String>> connectorCavities = new HashedMap<>();
        for(String connector:uniqueConnectors){
            connectorCavities.put(connector,RowUtil.getColumn(RowUtil.getRowsByColumnValue(maxTable,connector,MAX_TO_CONNECTOR),MAX_TO_CAVITY));
        }

        //remove duplicate cavities from the connectorCavities map

        for(String connector:uniqueConnectors){
            List<String> cavities = connectorCavities.get(connector);
            int cavitiesLength = cavities.size();
            for(int i =0;i<cavitiesLength;i++){
                for(int j=i+1;j<cavitiesLength;j++){
                    if(cavities.get(i).equals(cavities.get(j))){
                        cavities.remove(j);
                        j--;
                        cavitiesLength--;
                    }
                }
            }
        }

        Map<String,Map<String,List<String>>> connectorCavityWires = new HashedMap<>();

        for(String connector:uniqueConnectors){
            Map<String,List<String>> cavitiesForEachConnector = new HashedMap<>();
            connectorCavityWires.put(connector,cavitiesForEachConnector);


            for(String cavity:connectorCavities.get(connector)){
                List<String> wiresForEachCavity = new ArrayList<>();

                List<List<String>> connectorQuery = RowUtil.getRowsByColumnValue(maxTable,connector,MAX_TO_CONNECTOR);
                List<List<String>> cavityQuery = RowUtil.getRowsByColumnValue(maxTable,cavity,MAX_TO_CAVITY);

                for(List<String> connectorRow : connectorQuery){
                    for (List<String> cavityRow : cavityQuery){
                        if(connectorRow.get(MAX_TO_CONNECTOR).equals(cavityRow.get(MAX_TO_CONNECTOR)) && connectorRow.get(MAX_TO_CAVITY).equals(cavityRow.get(MAX_TO_CAVITY))){
                            if(!wiresForEachCavity.contains(connectorRow.get(maxTable.get(0).size()-1))){
                                wiresForEachCavity.add(connectorRow.get(maxTable.get(0).size()-1));
                            }
                        }
                    }
                }
                cavitiesForEachConnector.put(cavity,wiresForEachCavity);
            }
        }

        System.out.println(connectorCavities.size());
        System.out.println(connectorCavityWires);






















































//        for(int i=1;i<maxTable.size();i++){
//
//            List<String> rowFrom = RowUtil.emptyRow(crimpingTable.get(0).size());
//
//            rowFrom.set(CRIMPING_WIRE_COSTUMER_POSITION,maxTable.get(i).get(MAX_WIRE_COSTUMER));
//
//            rowFrom.set(CRIMPING_INTERNAL,maxTable.get(i).get(MAX_FROM_INTERNAL));
//
//            rowFrom.set(COSTUMER_CONNECTOR_IN_CRIMPING,maxTable.get(i).get(MAX_FROM_CONNECTOR));
//
//            rowFrom.set(CAVITY_ORDER_IN_CRIMPING,maxTable.get(i).get(MAX_FROM_CAVITY));
//
//            rowFrom.set(FMC_ORDER_IN_CRIMPING,maxTable.get(i).get(FMC_ORDER_IN_MAX));
//
//            finalCrimpingFile.add(rowFrom);
//
//            List<String> rowTo = RowUtil.emptyRow(crimpingTable.get(0).size());
//
//            rowTo.set(CRIMPING_WIRE_COSTUMER_POSITION,maxTable.get(i).get(MAX_WIRE_COSTUMER));
//
//            rowTo.set(CRIMPING_INTERNAL,maxTable.get(i).get(MAX_TO_INTERNAL));
//
//            rowTo.set(COSTUMER_CONNECTOR_IN_CRIMPING,maxTable.get(i).get(MAX_TO_CONNECTOR));
//
//            rowTo.set(CAVITY_ORDER_IN_CRIMPING,maxTable.get(i).get(MAX_TO_CAVITY));
//
//            rowTo.set(FMC_ORDER_IN_CRIMPING,maxTable.get(i).get(FMC_ORDER_IN_MAX));
//
//            finalCrimpingFile.add(rowTo);
//        }

        ExportData.exportTableToExcel(RESOURCES_FOLDER+"did.xlsx","today",finalCrimpingFile);

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

            Map<String,List<String>> plausibilitySubMap = new HashedMap<>();

            plausibilitySubMap.put("v",matching);
            plausibilitySubMap.put("n",nonMatching);

            for(int j=2;j<plausibilityTable.get(0).size();j++){

                if(plausibilityTable.get(i).get(j).equalsIgnoreCase("v") && !plausibilitySubMap.get("v").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("v").add(plausibilityTable.get(0).get(j));
                }else if(plausibilityTable.get(i).get(j).equalsIgnoreCase("n") && !plausibilitySubMap.get("n").contains(plausibilityTable.get(0).get(j))){
                    plausibilitySubMap.get("n").add(plausibilityTable.get(0).get(j));
                }

            }

            plausibilityMap.put(fmp,plausibilitySubMap);
        }

        return plausibilityMap;
    }

//    private void getCrimpingTable;
}
