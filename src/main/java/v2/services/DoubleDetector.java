package v2.services;

import v2.helpers.Values;
import v2.utils.ExportData;
import v2.utils.ImportData;
import v2.utils.RowUtil;
import v2.helpers.Values;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static v2.helpers.Values.*;

/**
 * Detect non Valid rows in Max Report file by comparing it to the Crimping File
 * and take action to modify or add new rows
 */
public class DoubleDetector {
    List<List<String>> crimpingTable;
    List<List<String>> maxTable;
    int maxTableIdPosition;
    int crimpingTableIdPosition;
    List<List<String>> finalTable;
    List<String> modifiedRows;






    public DoubleDetector(String maxWiresList, String crimpingReport) throws IOException {
        this.crimpingTable = ImportData.importWorkSheet(crimpingReport, CRIMPING_ORDER_IN_SHEET);
        this.maxTable = ImportData.importWorkSheet(maxWiresList,MAX_ORDER_IN_SHEET);
    }


    public void initializeData() throws IOException {

        // first step get wires that exists in both crimping and max
        List<String> crimpingWires = RowUtil.getColumn(crimpingTable,Values.CRIMPING_WIRE_COSTUMER_POSITION);
        List<String> maxWires = RowUtil.getColumn(maxTable,CRIMPING_WIRE_COSTUMER_POSITION);

        maxTableIdPosition = maxTable.get(0).size();
        crimpingTableIdPosition = crimpingTable.get(0).size();

        RowUtil.addId(maxTable,maxTableIdPosition);
        RowUtil.addId(crimpingTable,crimpingTableIdPosition);

        RowUtil.removeDuplicatedRows(maxTable);
        RowUtil.removeDuplicatedRows(crimpingTable);

        finalTable = RowUtil.duplicateTable(maxTable);

        List<String> commonWires = RowUtil.commonValues(crimpingWires,maxWires);
        // remove the header value
        commonWires.remove(0);

        List<List<String>> expandedCrimpingTable = expandCrimping();
        modifiedRows = new ArrayList<>();

        // check for each common wire
        for(String commonWire:commonWires){
            List<List<String>> maxSubTable = RowUtil.getRowsByColumnValue(maxTable,commonWire,MAX_WIRE_COSTUMER);
            List<List<String>> crimpingSubTable = RowUtil.getRowsByColumnValue(expandedCrimpingTable,commonWire,CRIMPING_WIRE_COSTUMER_POSITION);

            for(List<String> crimpingSubRow:crimpingSubTable){
                String connector = crimpingSubRow.get(COSTUMER_CONNECTOR_IN_CRIMPING);
                String cavity = crimpingSubRow.get(CAVITY_ORDER_IN_CRIMPING);
                String crimpingType = crimpingSubRow.get(CRIMPING_TYPE_IN_CRIMPING_REPORT);
                String models = crimpingSubRow.get(FMC_ORDER_IN_CRIMPING);
                String crimpingPair = crimpingSubRow.get(CRIMPING_DOUBLE);
                String checkOn = "";
                List<List<String>> matchingWires = new ArrayList<>();
                boolean foundMatch = false;

                for(List<String> maxSubRow:maxSubTable){
                    if(maxSubRow.get(MAX_FROM_CAVITY).equals(cavity) && maxSubRow.get(MAX_FROM_CONNECTOR).equals(connector) && models.contains(maxSubRow.get(FMC_ORDER_IN_MAX))){
                        checkOn = "source";
                        matchingWires.add(maxSubRow);
                        if(maxSubRow.get(MAX_FROM_WIRE_TYPE).equalsIgnoreCase(crimpingType)){
                            foundMatch = true;
                        }
                    }

                    if(maxSubRow.get(MAX_TO_CAVITY).equals(cavity) && maxSubRow.get(MAX_TO_CONNECTOR).equals(connector) && models.contains(maxSubRow.get(FMC_ORDER_IN_MAX))){
                        checkOn = "destination";
                        matchingWires.add(maxSubRow);
                        if(maxSubRow.get(MAX_TO_WIRE_TYPE).equalsIgnoreCase(crimpingType)){
                            foundMatch = true;
                        }
                    }
                }

                if(!foundMatch && matchingWires.size()>0) {
                    //save a copy of the wires
                    List<String>  copy = RowUtil.duplicateRow(matchingWires.get(0));

                    //check if the matching wires are extra wires or not
                    for(int i =0 ; i < matchingWires.size() ; i++){
                        for(List<String> crimpingWire:crimpingSubTable){
                            if(matchingWires.get(i).get(MAX_FROM_CAVITY).equals(crimpingWire.get(CAVITY_ORDER_IN_CRIMPING)) && matchingWires.get(i).get(MAX_FROM_CONNECTOR).equals(crimpingWire.get(COSTUMER_CONNECTOR_IN_CRIMPING))){
                                if(matchingWires.get(i).get(MAX_FROM_WIRE_TYPE).equalsIgnoreCase(crimpingType)){
                                    matchingWires.remove(i);
                                    i--;
                                    break;
                                }
                            }

                            if(matchingWires.get(i).get(MAX_TO_CAVITY).equals(crimpingWire.get(CAVITY_ORDER_IN_CRIMPING)) && matchingWires.get(i).get(MAX_TO_CONNECTOR).equals(crimpingWire.get(COSTUMER_CONNECTOR_IN_CRIMPING))){
                                if(matchingWires.get(i).get(MAX_TO_WIRE_TYPE).equalsIgnoreCase(crimpingType)){
                                    matchingWires.remove(i);
                                    i--;
                                    break;
                                }
                            }
                        }
                    }

                    // add new row
                    if(matchingWires.size()==0){
                        if(checkOn.equalsIgnoreCase("source")){
                            copy.set(MAX_FROM_WIRE_TYPE,crimpingType);
                            if(crimpingType.equalsIgnoreCase("double")){
                                String doubleCrimping = (crimpingPair.contains(copy.get(MAX_WIRE_COSTUMER)+","))?crimpingPair.replace(copy.get(MAX_WIRE_COSTUMER)+",","") : crimpingPair.replace(","+copy.get(MAX_WIRE_COSTUMER),"");
                                copy.set(MAX_FROM_WIRE_DOUBLE,doubleCrimping);
                            }
                            if(crimpingType.equalsIgnoreCase("single")){
                                copy.set(MAX_FROM_WIRE_DOUBLE,"-");
                            }
                        }
                        if(checkOn.equalsIgnoreCase("destination")){
                            copy.set(MAX_TO_WIRE_TYPE,crimpingType);
                            if(crimpingType.equalsIgnoreCase("double")){
                                String doubleCrimping = (crimpingPair.contains(copy.get(MAX_WIRE_COSTUMER)+","))?crimpingPair.replace(copy.get(MAX_WIRE_COSTUMER)+",","") : crimpingPair.replace(","+copy.get(MAX_WIRE_COSTUMER),"");
                                copy.set(MAX_TO_WIRE_DOUBLE,doubleCrimping);
                            }
                            if(crimpingType.equalsIgnoreCase("single")){
                                copy.set(MAX_TO_WIRE_DOUBLE,"-");
                            }
                        }
                        copy.add("Added");
                        finalTable.add(copy);
                    }else{
                        if(checkOn.equalsIgnoreCase("source")){
                            copy.set(MAX_FROM_WIRE_TYPE,crimpingType);
                            if(crimpingType.equalsIgnoreCase("double")){
                                String doubleCrimping = (crimpingPair.contains(copy.get(MAX_WIRE_COSTUMER)+","))?crimpingPair.replace(copy.get(MAX_WIRE_COSTUMER)+",","") : crimpingPair.replace(","+copy.get(MAX_WIRE_COSTUMER),"");
                                copy.set(MAX_FROM_WIRE_DOUBLE,doubleCrimping);
                            }
                            if(crimpingType.equalsIgnoreCase("single")){
                                copy.set(MAX_FROM_WIRE_DOUBLE,"-");
                            }
                        }
                        if(checkOn.equalsIgnoreCase("destination")){
                            copy.set(MAX_TO_WIRE_TYPE,crimpingType);
                            if(crimpingType.equalsIgnoreCase("double")){
                                String doubleCrimping = (crimpingPair.contains(copy.get(MAX_WIRE_COSTUMER)+","))?crimpingPair.replace(copy.get(MAX_WIRE_COSTUMER)+",","") : crimpingPair.replace(","+copy.get(MAX_WIRE_COSTUMER),"");
                                copy.set(MAX_TO_WIRE_DOUBLE,doubleCrimping);
                            }
                            if(crimpingType.equalsIgnoreCase("single")){
                                copy.set(MAX_TO_WIRE_DOUBLE,"-");
                            }

                        }

                        copy.add("Modified");
                        if(!modifiedRows.contains(copy.get(maxTableIdPosition))) {
                            modifiedRows.add(copy.get(maxTableIdPosition));
                        }
                        finalTable.add(copy);
                    }
                }
            }

        }
    }

    public void prepareFinalTable(){

        //remove rows that are modified
        for(int i = 0 ; i < finalTable.size() ; i++){
            if(modifiedRows.contains(finalTable.get(i).get(maxTableIdPosition)) && finalTable.get(i).size()==maxTableIdPosition+1){
                finalTable.remove(i);
                i--;
            }
        }

        //remove id
        for(int i = 0 ; i < finalTable.size() ; i++){

            finalTable.get(i).remove(maxTableIdPosition);

        }

        finalTable.get(0).add("comment");


    }

    public void exportFinalTable(String fileName,String sheetName) throws IOException {
        ExportData.exportTableToExcel(fileName,sheetName,finalTable);
    }

    public List<List<String>> expandCrimping() {
        List<List<String>> expandedCrimpingTable = new ArrayList<>();

        for (List<String> crimpingRow : crimpingTable) {
            if (crimpingRow.get(FMC_ORDER_IN_CRIMPING).contains("/")) {
                String[] fmcS = crimpingRow.get(FMC_ORDER_IN_CRIMPING).split(" / ");
                for (String fmc : fmcS) {
                    List<String> newRow = RowUtil.duplicateRow(crimpingRow);
                    newRow.set(FMC_ORDER_IN_CRIMPING, fmc);
                    expandedCrimpingTable.add(newRow);
                }
            } else {
                expandedCrimpingTable.add(crimpingRow);
            }
        }

        return expandedCrimpingTable;
    }
}
