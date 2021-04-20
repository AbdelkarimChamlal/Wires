package v2.services;

import v2.exceptions.TemplateNotValid;
import v2.utils.*;
import v3.utils.JavaUtil;

import java.io.IOException;
import java.util.List;

public class CuttingDataCompact {
    List<List<String>> maxWireList;

    public CuttingDataCompact(String maxWireListPath) throws IOException {
        this.maxWireList = ImportData.importWorkSheet(maxWireListPath,0);
        boolean matchingTemplate = TemplateUtil.matchTemplate("max.xlsx",maxWireList.get(0));
        if(!matchingTemplate){
            throw new TemplateNotValid("Template of input doesn't match",new Throwable("header not valid"));
        }
    }


    public void initializeData(){
        //simply add those columns to the table
        maxWireList.get(0).add("DOUBLE PM");
        maxWireList.get(0).add("DOUBLE SK");
        maxWireList.get(0).add("TWIST PM");
        maxWireList.get(0).add("TWIST SK");

        for(int i = 1 ; i < maxWireList.size() ; i++){
            maxWireList.get(i).add("");
            maxWireList.get(i).add("");
            maxWireList.get(i).add("");
            maxWireList.get(i).add("");
        }

        int wireTypePosition = maxWireList.get(0).indexOf("Wire Type");
        int fromCrimpingType = maxWireList.get(0).indexOf("From Crimping Type");
        int fromCrimpingDouble = maxWireList.get(0).indexOf("From Double Crimp. With Wire(s)");
        int toCrimpingType = maxWireList.get(0).indexOf("To Crimping Type");
        int toCrimpingDouble = maxWireList.get(0).indexOf("To Double Crimp. With Wire(s)");
        int wireCostumerName = maxWireList.get(0).indexOf("Wire Customer Name");
        int doublePM = maxWireList.get(0).indexOf("DOUBLE PM");
        int doubleSK = maxWireList.get(0).indexOf("DOUBLE SK");
        int twistPM = maxWireList.get(0).indexOf("TWIST PM");
        int twistSK = maxWireList.get(0).indexOf("TWIST SK");

        for(int i = 1 ; i < maxWireList.size() ; i++){

            if(maxWireList.get(i).get(wireTypePosition).equalsIgnoreCase("Twisted Wire")){
                maxWireList.get(i).set(twistPM,"541"+maxWireList.get(i).get(wireCostumerName)+"_1A");
            }

            if(maxWireList.get(i).get(fromCrimpingType).equalsIgnoreCase("double")){
                String sortAndConcat = JavaUtil.sortAndConcat(JavaUtil.convertArrayToList(new String[] {
                        maxWireList.get(i).get(wireCostumerName),
                        maxWireList.get(i).get(fromCrimpingDouble)
                }));
                maxWireList.get(i).set(doublePM,"521"+sortAndConcat+"_1A");
            }

            if(maxWireList.get(i).get(toCrimpingType).equalsIgnoreCase("double")){
                String sortAndConcat = JavaUtil.sortAndConcat(JavaUtil.convertArrayToList(new String[] {
                        maxWireList.get(i).get(wireCostumerName),
                        maxWireList.get(i).get(toCrimpingDouble)
                }));
                maxWireList.get(i).set(doublePM,"521"+sortAndConcat+"_1A");
            }

        }


    }



    public void exportData(String filePath,String sheetName) throws IOException {
        ExportData.exportTableUsingTemplate(filePath,maxWireList,"cuttingDataCompacted.xlsx");
    }


}
