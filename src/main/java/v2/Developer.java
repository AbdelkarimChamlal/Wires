package v2;

import v2.helpers.Values;
import v2.services.ChangeDetector;
import v2.services.CompatibilityDetector;
import v2.services.DoubleDetector;
import v2.services.MaxChangeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the play ground while am developing this project
 */
public class Developer {

    public static void main(String[] args) {

        // let us develop some CompatibilityDetector

        // testing the compatibilityDetector
//        try{
//            // initialize that badBoy
//            CompatibilityDetector compatibilityDetector = new CompatibilityDetector(Values.RESOURCES_FOLDER +"max.xlsx",Values.RESOURCES_FOLDER+"plausibility.xlsm",Values.RESOURCES_FOLDER+"crimping.xlsx");
//
//            //start the comparing process
//            compatibilityDetector.startComparing();
//
//            //get list of invalid doubles
//            compatibilityDetector.compareCrimping();
//
//            //export the list of non matching wires
//            compatibilityDetector.exportErrors("crimpingErrors.xlsx");
//
//
//        }catch(IOException error_message){
//            System.out.println("Failed to read files\n"+error_message);
//        }

        //testing the DoubleDetector
//        try{
//            DoubleDetector doubleDetector = new DoubleDetector(Values.RESOURCES_FOLDER +"max.xlsx",Values.RESOURCES_FOLDER+"resources.xlsx");
//            doubleDetector.initializeData();
//            doubleDetector.prepareFinalTable();
//            doubleDetector.exportFinalTable(Values.RESOURCES_FOLDER+"finalMax.xlsx","finalMax");
//
//
//        }catch(IOException e){
//            System.out.println(e.getMessage());
//        }

        //testing change detector
//        try{
//            List<Integer> uniqueKeys = new ArrayList<>();
//
//            uniqueKeys.add(Values.FMC_ORDER_IN_CRIMPING);
//            uniqueKeys.add(Values.CRIMPING_WIRE_COSTUMER_POSITION);
//            uniqueKeys.add(Values.COSTUMER_CONNECTOR_IN_CRIMPING);
//            uniqueKeys.add(Values.CAVITY_ORDER_IN_CRIMPING);
//            uniqueKeys.add(Values.CRIMPING_TYPE_IN_CRIMPING_REPORT);
//
//            ChangeDetector changeDetector = new ChangeDetector(Values.RESOURCES_FOLDER+"crimpingTest.xlsx",Values.RESOURCES_FOLDER+"resources Copy.xlsx",uniqueKeys);
//
//            changeDetector.initializeData();
//
//            changeDetector.prepareFinalData();
//
//            changeDetector.exportChangedLog(Values.RESOURCES_FOLDER+"changesLog.xlsx","log");
//
//        }catch(IOException e){
//            System.out.println("Error on the level of IO");
//        }

        try{
            MaxChangeDetector maxChangeDetector = new MaxChangeDetector(Values.RESOURCES_FOLDER+"max.xlsx",Values.RESOURCES_FOLDER+"new max.xlsx");
            maxChangeDetector.addPrimaryKeys();
            maxChangeDetector.initializeData();
            maxChangeDetector.prepareFinalData();
            maxChangeDetector.exportChangedLog(Values.RESOURCES_FOLDER+"testingData.xlsx","max logs");
        }catch(IOException e){
            System.out.println("something Happened here"+e);
        }

    }
}
