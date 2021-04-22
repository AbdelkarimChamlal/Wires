package v2;

import v2.helpers.Values;
import v2.services.*;
import v2.utils.ImportData;
import v2.utils.JavaUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the play ground while am developing this project
 */
public class Developer {

    public static void main(String[] args) throws IOException {

//          let us develop some CompatibilityDetector
//          testing the compatibilityDetector
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
//          testing the DoubleDetector
//        try{
//            DoubleDetector doubleDetector = new DoubleDetector(Values.RESOURCES_FOLDER +"max.xlsx",Values.RESOURCES_FOLDER+"crimping.xlsx");
//            doubleDetector.initializeData();
//            doubleDetector.prepareFinalTable();
//            doubleDetector.exportFinalTable(Values.RESOURCES_FOLDER+"finalMax.xlsx","finalMax");
//
//
//        }catch(IOException e){
//            System.out.println(e.getMessage());
//        }
//          testing change detector
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
//        try{
//            MaxChangeDetector maxChangeDetector = new MaxChangeDetector(Values.RESOURCES_FOLDER+"newMAXSKPM.xlsx",Values.RESOURCES_FOLDER+"max.xlsx");
//            maxChangeDetector.addPrimaryKeys();
//            maxChangeDetector.initializeData();
//            maxChangeDetector.prepareFinalData();
//
//            List<String> allDirectories = ImportData.getAllDirectories("uploads");
//            int latestDirectory = JavaUtils.maxValueOfList(JavaUtils.convertListToIntegers(allDirectories));
//            latestDirectory++;
//            JavaUtils.createNewDirectory("uploads/",latestDirectory+"");
//
//            maxChangeDetector.exportChangedLog("uploads/"+latestDirectory+"/Cutting Data.xlsx","Wire List");
//        }catch(IOException e){
//            System.out.println("something Happened here"+e);
//        }
//        try{
//            CuttingDataGenerator cuttingDataGenerator = new CuttingDataGenerator(Values.RESOURCES_FOLDER+"maxSKPM.xlsx");
//            cuttingDataGenerator.initializeData();
//            cuttingDataGenerator.prepareFinalData();
//            cuttingDataGenerator.exportCuttingData("cuttingOutput.xlsx");
//        }catch(IOException e){
//            System.out.println(e);
//        }

        try{
            CuttingDataCompact cuttingDataCompact = new CuttingDataCompact("uploads/cuttingData.xlsx");
            cuttingDataCompact.initializeData();
            cuttingDataCompact.exportData("uploads/testRevision.xlsx","cuttingData");
        }catch (Exception e){
            System.out.println(e);
            throw e;
        }

    }
}
